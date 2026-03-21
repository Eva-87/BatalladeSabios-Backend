package com.batalla.demoquiz.service;

import com.batalla.demoquiz.dto.AnswerMessage;
import com.batalla.demoquiz.dto.GameOverMessage;
import com.batalla.demoquiz.dto.QuestionMessage;
import com.batalla.demoquiz.dto.RoundResultMessage;
import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.Question;
import com.batalla.demoquiz.entity.RoomPlayer;
import com.batalla.demoquiz.enums.GameStatus;
import com.batalla.demoquiz.repository.GameRoomRepository;
import com.batalla.demoquiz.repository.RoomPlayerRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameEngineServiceImpl implements GameEngineService {

    private final GameRoomRepository gameRoomRepository;
    private final RoomPlayerRepository roomPlayerRepository;

    private final Map<String, List<Question>> roomQuestions = new HashMap<>();
    private final Map<String, Integer> roomRound = new HashMap<>();
    private final Map<String, Map<Long, Integer>> roomAnswers = new HashMap<>();
    private final Map<String, Long> questionStartTime = new HashMap<>();

    public GameEngineServiceImpl(GameRoomRepository gameRoomRepository,
                                 RoomPlayerRepository roomPlayerRepository) {
        this.gameRoomRepository = gameRoomRepository;
        this.roomPlayerRepository = roomPlayerRepository;
    }

    // ---------------------------------------------------------
    // START GAME
    // ---------------------------------------------------------
    @Override
    public QuestionMessage startGame(String roomCode) {
        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Question> questions = room.getQuiz().getQuestions();
        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("Quiz has no questions");
        }

        // Mezclar preguntas
        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);

        roomQuestions.put(roomCode, shuffled);
        roomRound.put(roomCode, 1); // ⭐ Empezamos en ronda 1
        roomAnswers.put(roomCode, new HashMap<>());

        room.setStatus(GameStatus.PLAYING);
        gameRoomRepository.save(room);

        // ⭐ Devolver la PRIMERA pregunta directamente
        Question q = shuffled.get(0);

        questionStartTime.put(roomCode, System.currentTimeMillis());

        return new QuestionMessage(
                q.getId(),
                q.getText(),
                List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()),
                1,
                q.getCorrectIndex(),
                q.getExplanation()
        );
    }

    // ---------------------------------------------------------
    // SUBMIT ANSWER
    // ---------------------------------------------------------
    @Override
    public RoundResultMessage submitAnswer(String roomCode, AnswerMessage answer) {
        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        roomAnswers.computeIfAbsent(roomCode, k -> new HashMap<>())
                .put(answer.getUserId(), answer.getChosenIndex());

        List<RoomPlayer> players = roomPlayerRepository.findByRoom(room);

        // Esperar a que todos respondan
        if (roomAnswers.get(roomCode).size() < players.size()) {
            return null;
        }

        int round = roomRound.get(roomCode);
        List<Question> questions = roomQuestions.get(roomCode);

        Question q = questions.get(round - 1);

        Map<Long, Integer> scores = new HashMap<>();

        long start = questionStartTime.getOrDefault(roomCode, System.currentTimeMillis());
        long now = System.currentTimeMillis();
        int secondsElapsed = (int) ((now - start) / 1000);

        for (RoomPlayer p : players) {
            Long userId = p.getUser().getId();
            Integer chosen = roomAnswers.get(roomCode).get(userId);

            if (chosen != null && chosen == q.getCorrectIndex()) {
                int timeBonus = Math.max(0, 100 - secondsElapsed * 10);
                p.setScore(p.getScore() + timeBonus);
                roomPlayerRepository.save(p);

                room.addCorrect(userId);
            } else {
                room.addWrong(userId);
            }

            scores.put(userId, p.getScore());
        }

        roomAnswers.get(roomCode).clear();

        room.setStatus(GameStatus.SHOWING_RESULTS);
        gameRoomRepository.save(room);

        return new RoundResultMessage(
                q.getId(),
                q.getCorrectIndex(),
                scores
        );
    }

    // ---------------------------------------------------------
    // NEXT QUESTION
    // ---------------------------------------------------------
    @Override
    public QuestionMessage nextQuestion(String roomCode) {
        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Question> questions = roomQuestions.get(roomCode);

        int round = roomRound.compute(roomCode, (k, v) -> v + 1);

        if (round > questions.size()) {
            room.setStatus(GameStatus.FINISHED);
            gameRoomRepository.save(room);
            return null;
        }

        Question q = questions.get(round - 1);

        questionStartTime.put(roomCode, System.currentTimeMillis());

        room.setStatus(GameStatus.PLAYING);
        gameRoomRepository.save(room);

        return new QuestionMessage(
                q.getId(),
                q.getText(),
                List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()),
                round,
                q.getCorrectIndex(),
                q.getExplanation()
        );
    }

    // ---------------------------------------------------------
    // FINISH GAME
    // ---------------------------------------------------------
    @Override
    public GameOverMessage finishGame(String roomCode) {
        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setStatus(GameStatus.FINISHED);
        gameRoomRepository.save(room);

        List<RoomPlayer> players = roomPlayerRepository.findByRoom(room);

        int totalQuestions = roomQuestions.get(roomCode).size();

        List<GameOverMessage.PlayerResult> ranking = players.stream()
                .sorted(Comparator.comparingInt(RoomPlayer::getScore).reversed())
                .map(p -> new GameOverMessage.PlayerResult(
                        p.getUser().getId(),
                        p.getUser().getUsername(),
                        p.getScore(),
                        room.getCorrectAnswers().getOrDefault(p.getUser().getId(), 0),
                        totalQuestions
                ))
                .collect(Collectors.toList());

        return new GameOverMessage(ranking);
    }
}

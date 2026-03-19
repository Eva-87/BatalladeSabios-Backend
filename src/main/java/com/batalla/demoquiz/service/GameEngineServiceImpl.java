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

    // Preguntas por sala
    private final Map<String, List<Question>> roomQuestions = new HashMap<>();
    // Ronda actual por sala (1-based)
    private final Map<String, Integer> roomRound = new HashMap<>();
    // Respuestas por sala: roomCode -> (userId -> chosenIndex)
    private final Map<String, Map<Long, Integer>> roomAnswers = new HashMap<>();
    // Tiempo de inicio de cada pregunta por sala
    private final Map<String, Long> questionStartTime = new HashMap<>();

    public GameEngineServiceImpl(GameRoomRepository gameRoomRepository,
                                 RoomPlayerRepository roomPlayerRepository) {
        this.gameRoomRepository = gameRoomRepository;
        this.roomPlayerRepository = roomPlayerRepository;
    }

    @Override
    public QuestionMessage startGame(String roomCode) {
        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Question> questions = room.getQuiz().getQuestions();
        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("Quiz has no questions");
        }

        // Mezclamos preguntas
        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);

        roomQuestions.put(roomCode, shuffled);
        roomRound.put(roomCode, 0);
        roomAnswers.put(roomCode, new HashMap<>());

        room.setStatus(GameStatus.PLAYING);
        gameRoomRepository.save(room);

        return nextQuestion(roomCode);
    }

    @Override
    public RoundResultMessage submitAnswer(String roomCode, AnswerMessage answer) {
        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Guardar respuesta del jugador
        roomAnswers
                .computeIfAbsent(roomCode, k -> new HashMap<>())
                .put(answer.getUserId(), answer.getChosenIndex());

        List<RoomPlayer> players = roomPlayerRepository.findByRoom(room);

        // Si aún no respondieron todos, no devolvemos resultados
        if (roomAnswers.get(roomCode).size() < players.size()) {
            return null;
        }

        int round = roomRound.get(roomCode);
        List<Question> questions = roomQuestions.get(roomCode);
        if (questions == null || round <= 0 || round > questions.size()) {
            throw new RuntimeException("Invalid round state");
        }

        Question q = questions.get(round - 1);

        Map<Long, Integer> scores = new HashMap<>();

        long start = questionStartTime.getOrDefault(roomCode, System.currentTimeMillis());
        long now = System.currentTimeMillis();
        int secondsElapsed = (int) ((now - start) / 1000);

        for (RoomPlayer p : players) {
            Long userId = p.getUser().getId();
            Integer chosen = roomAnswers.get(roomCode).get(userId);

            if (chosen != null && chosen == q.getCorrectIndex()) {
                // Bonus por rapidez (ejemplo simple)
                int timeBonus = Math.max(0, 100 - secondsElapsed * 10);
                p.setScore(p.getScore() + timeBonus);
                roomPlayerRepository.save(p);
            }

            scores.put(userId, p.getScore());
        }

        // Limpiamos respuestas para la siguiente ronda
        roomAnswers.get(roomCode).clear();

        room.setStatus(GameStatus.SHOWING_RESULTS);
        gameRoomRepository.save(room);

        return new RoundResultMessage(
                q.getId(),
                q.getCorrectIndex(),
                scores
        );
    }

    @Override
    public QuestionMessage nextQuestion(String roomCode) {
        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Question> questions = roomQuestions.get(roomCode);
        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("No questions loaded for this room");
        }

        int round = roomRound.compute(roomCode, (k, v) -> (v == null ? 1 : v + 1));

        if (round > questions.size()) {
            // No hay más preguntas
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
                round
        );
    }

    @Override
    public GameOverMessage finishGame(String roomCode) {
        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setStatus(GameStatus.FINISHED);
        gameRoomRepository.save(room);

        List<RoomPlayer> players = roomPlayerRepository.findByRoom(room);

        List<GameOverMessage.PlayerResult> ranking = players.stream()
                .sorted(Comparator.comparingInt(RoomPlayer::getScore).reversed())
                .map(p -> new GameOverMessage.PlayerResult(
                        p.getUser().getId(),
                        p.getUser().getUsername(),
                        p.getScore()
                ))
                .collect(Collectors.toList());

        return new GameOverMessage(ranking);
    }
}

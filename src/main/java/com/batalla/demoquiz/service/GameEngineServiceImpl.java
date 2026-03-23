package com.batalla.demoquiz.service;

import com.batalla.demoquiz.dto.AnswerMessage;
import com.batalla.demoquiz.dto.GameOverMessage;
import com.batalla.demoquiz.dto.QuestionMessage;
import com.batalla.demoquiz.dto.RoundResultMessage;
import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.PlayerAnswer;
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
    private final PlayerAnswerService playerAnswerService;

    public GameEngineServiceImpl(
            GameRoomRepository gameRoomRepository,
            RoomPlayerRepository roomPlayerRepository,
            PlayerAnswerService playerAnswerService
    ) {
        this.gameRoomRepository = gameRoomRepository;
        this.roomPlayerRepository = roomPlayerRepository;
        this.playerAnswerService = playerAnswerService;
    }

    @Override
    public QuestionMessage startGame(String roomCode) {

        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Question> questions = room.getQuiz().getQuestions();
        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("Quiz has no questions");
        }

        Collections.shuffle(questions);

        room.setCurrentQuestionIndex(0);
        room.setLastRoundResult(null);
        room.setStatus(GameStatus.PLAYING);
        gameRoomRepository.save(room);

        return buildQuestionMessage(questions.get(0), 1);
    }

    @Override
    public RoundResultMessage submitAnswer(String roomCode, AnswerMessage answer) {

        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Question> questions = room.getQuiz().getQuestions();
        int index = room.getCurrentQuestionIndex();
        Question q = questions.get(index);

        List<RoomPlayer> players = roomPlayerRepository.findByRoom(room);

        RoomPlayer player = players.stream()
                .filter(p -> p.getUser().getId().equals(answer.getUserId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player not in room"));

        if (!playerAnswerService.hasAnswered(room, player.getUser(), index)) {

            boolean correct = answer.getChosenIndex() == q.getCorrectIndex();

            playerAnswerService.saveAnswer(room, player.getUser(), index, correct);

            if (correct) {
                player.setScore(player.getScore() + 100);
            }

            roomPlayerRepository.save(player);
        }

        int answered = playerAnswerService.countAnswers(room, index);
        int totalPlayers = players.size();

        if (answered == totalPlayers) {
            room.setStatus(GameStatus.SHOWING_RESULTS);
        }

        // 🔥 Puntajes congelados de la ronda
        Map<Long, Integer> scores = players.stream()
                .collect(Collectors.toMap(
                        p -> p.getUser().getId(),
                        p -> p.getScore()
                ));

        RoundResultMessage result = new RoundResultMessage(
                q.getId(),
                q.getCorrectIndex(),
                scores
        );

        room.setLastRoundResult(result);
        gameRoomRepository.save(room);

        return result;
    }

    @Override
    public QuestionMessage nextQuestion(String roomCode) {

        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Question> questions = room.getQuiz().getQuestions();

        int nextIndex = room.getCurrentQuestionIndex() + 1;

        if (nextIndex >= questions.size()) {
            room.setStatus(GameStatus.FINISHED);
            room.setLastRoundResult(null);
            gameRoomRepository.save(room);
            return new QuestionMessage(null, null, List.of(), -1, -1, null);
        }

        playerAnswerService.clearAnswersForQuestion(room, room.getCurrentQuestionIndex());

        room.setLastRoundResult(null);
        room.setCurrentQuestionIndex(nextIndex);
        room.setStatus(GameStatus.PLAYING);
        gameRoomRepository.save(room);

        return buildQuestionMessage(questions.get(nextIndex), nextIndex + 1);
    }

    @Override
    public GameOverMessage finishGame(String roomCode) {

        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setStatus(GameStatus.FINISHED);
        room.setLastRoundResult(null);
        gameRoomRepository.save(room);

        // 🔥 Limpia respuestas de la última pregunta
        playerAnswerService.clearAnswersForQuestion(room, room.getCurrentQuestionIndex());

        List<RoomPlayer> players = roomPlayerRepository.findByRoom(room);

        int totalQuestions = room.getQuiz().getQuestions().size();

        List<GameOverMessage.PlayerResult> ranking = players.stream()
                .map(p -> {

                    long correctCount = playerAnswerService
                            .getAnswersForPlayer(room, p.getUser())
                            .stream()
                            .filter(PlayerAnswer::isCorrect)
                            .count();

                    return new GameOverMessage.PlayerResult(
                            p.getUser().getId(),
                            p.getUser().getUsername(),
                            p.getScore(),
                            (int) correctCount,
                            totalQuestions
                    );
                })
                .sorted(Comparator.comparingInt(GameOverMessage.PlayerResult::getScore).reversed())
                .toList();

        return new GameOverMessage(ranking);
    }

    @Override
    public QuestionMessage getCurrentQuestion(String roomCode) {

        GameRoom room = gameRoomRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Question> questions = room.getQuiz().getQuestions();
        int index = room.getCurrentQuestionIndex();

        if (index < 0 || index >= questions.size()) {
            return null;
        }

        Question q = questions.get(index);

        return new QuestionMessage(
                q.getId(),
                q.getText(),
                List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()),
                index + 1,
                q.getCorrectIndex(),
                q.getExplanation()
        );
    }

    private QuestionMessage buildQuestionMessage(Question q, int round) {
        return new QuestionMessage(
                q.getId(),
                q.getText(),
                List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()),
                round,
                q.getCorrectIndex(),
                q.getExplanation()
        );
    }
}

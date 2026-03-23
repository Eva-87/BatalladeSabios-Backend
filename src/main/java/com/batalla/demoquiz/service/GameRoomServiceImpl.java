package com.batalla.demoquiz.service;

import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.Quiz;
import com.batalla.demoquiz.entity.RoomPlayer;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.enums.GameStatus;
import com.batalla.demoquiz.repository.GameRoomRepository;
import com.batalla.demoquiz.repository.QuizRepository;
import com.batalla.demoquiz.repository.RoomPlayerRepository;
import com.batalla.demoquiz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GameRoomServiceImpl implements GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final Random random = new Random();

    public GameRoomServiceImpl(GameRoomRepository gameRoomRepository,
                               RoomPlayerRepository roomPlayerRepository,
                               UserRepository userRepository,
                               QuizRepository quizRepository) {
        this.gameRoomRepository = gameRoomRepository;
        this.roomPlayerRepository = roomPlayerRepository;
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
    }

    @Override
    public GameRoom createRoom(Long creatorId, Long quizId, int maxPlayers) {

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        GameRoom room = new GameRoom();
        room.setCode(generateCode());
        room.setQuiz(quiz);
        room.setCreator(creator);
        room.setMaxPlayers(maxPlayers);
        room.setStatus(GameStatus.LOBBY);
        room.setLastRoundResult(null);

        GameRoom saved = gameRoomRepository.save(room);

        // Registrar al creador como jugador
        RoomPlayer host = new RoomPlayer();
        host.setRoom(saved);
        host.setUser(creator);
        host.setScore(0);
        host.setConnected(true);
        roomPlayerRepository.save(host);

        return saved;
    }

    @Override
    public RoomPlayer joinRoom(String code, Long userId) {
        code = code.trim();

        GameRoom room = gameRoomRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoomPlayer player = new RoomPlayer();
        player.setRoom(room);
        player.setUser(user);
        player.setScore(0);
        player.setConnected(true);

        return roomPlayerRepository.save(player);
    }

    @Override
    public List<RoomPlayer> getPlayers(String code) {
        code = code.trim();

        GameRoom room = gameRoomRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        return roomPlayerRepository.findByRoom(room);
    }

    @Override
    public GameRoom getRoom(String code) {
        code = code.trim();

        return gameRoomRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public GameStatus getStatus(String code) {
        code = code.trim();

        return gameRoomRepository.findByCode(code)
                .map(GameRoom::getStatus)
                .orElse(GameStatus.LOBBY);
    }

    private String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

package com.batalla.demoquiz.service;

import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.Quiz;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.enums.GameStatus;
import com.batalla.demoquiz.repository.GameRoomRepository;
import com.batalla.demoquiz.repository.QuizRepository;
import com.batalla.demoquiz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private final GameRoomRepository roomRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public RoomServiceImpl(
            GameRoomRepository roomRepository,
            QuizRepository quizRepository,
            UserRepository userRepository
    ) {
        this.roomRepository = roomRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GameRoom createRoom(Long quizId, Long userId, int maxPlayers) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GameRoom room = new GameRoom();
        room.setQuiz(quiz);
        room.setCreator(creator);
        room.setMaxPlayers(maxPlayers);
        room.setCode(generateCode());
        room.setStatus(GameStatus.LOBBY);

        return roomRepository.save(room);
    }

    @Override
    public GameRoom getRoomByCode(String code) {
        return roomRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public GameStatus getStatus(String code) {
        return roomRepository.findByCode(code)
                .map(GameRoom::getStatus)
                .orElse(GameStatus.LOBBY);
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

package com.batalla.demoquiz.service;

import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.PlayerAnswer;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.repository.PlayerAnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerAnswerServiceImpl implements PlayerAnswerService {

    private final PlayerAnswerRepository repo;

    public PlayerAnswerServiceImpl(PlayerAnswerRepository repo) {
        this.repo = repo;
    }

    @Override
    public void saveAnswer(GameRoom room, User user, int questionIndex, boolean correct) {
        PlayerAnswer answer = new PlayerAnswer(room, user, questionIndex, correct);
        repo.save(answer);
    }

    @Override
    public boolean hasAnswered(GameRoom room, User user, int questionIndex) {
        return repo.existsByRoomAndUserAndQuestionIndex(room, user, questionIndex);
    }

    @Override
    public int countAnswers(GameRoom room, int questionIndex) {
        return repo.findByRoomAndQuestionIndex(room, questionIndex).size();
    }

    @Override
    public List<PlayerAnswer> getAnswersForPlayer(GameRoom room, User user) {
        return repo.findByRoomAndUser(room, user);
    }

    @Override
    @Transactional
    public void clearAnswersForQuestion(GameRoom room, int questionIndex) {
        repo.deleteByRoomAndQuestionIndex(room, questionIndex);
    }
}

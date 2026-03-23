package com.batalla.demoquiz.service;

import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.PlayerAnswer;
import com.batalla.demoquiz.entity.User;

import java.util.List;

public interface PlayerAnswerService {

    void saveAnswer(GameRoom room, User user, int questionIndex, boolean correct);

    boolean hasAnswered(GameRoom room, User user, int questionIndex);

    int countAnswers(GameRoom room, int questionIndex);

    List<PlayerAnswer> getAnswersForPlayer(GameRoom room, User user);

    void clearAnswersForQuestion(GameRoom room, int questionIndex);
}


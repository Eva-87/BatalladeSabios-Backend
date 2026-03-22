package com.batalla.demoquiz.service;

import com.batalla.demoquiz.dto.AnswerMessage;
import com.batalla.demoquiz.dto.GameOverMessage;
import com.batalla.demoquiz.dto.QuestionMessage;
import com.batalla.demoquiz.dto.RoundResultMessage;

public interface GameEngineService {

    QuestionMessage startGame(String roomCode);

    RoundResultMessage submitAnswer(String roomCode, AnswerMessage answer);

    QuestionMessage nextQuestion(String roomCode);

    GameOverMessage finishGame(String roomCode);

    QuestionMessage getCurrentQuestion(String roomCode);
}

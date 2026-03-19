package com.batalla.demoquiz.dto;

import java.util.Map;

public class RoundResultMessage {

    private Long questionId;
    private int correctIndex;
    private Map<Long, Integer> scores;

    public RoundResultMessage() {}

    public RoundResultMessage(Long questionId, int correctIndex, Map<Long, Integer> scores) {
        this.questionId = questionId;
        this.correctIndex = correctIndex;
        this.scores = scores; 
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }

    public Map<Long, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<Long, Integer> scores) {
        this.scores = scores;
    }
}

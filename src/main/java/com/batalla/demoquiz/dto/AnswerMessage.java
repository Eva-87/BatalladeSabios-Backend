package com.batalla.demoquiz.dto;

public class AnswerMessage {
    private Long userId;
    private Long questionId;
    private int chosenIndex;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public int getChosenIndex() { return chosenIndex; }
    public void setChosenIndex(int chosenIndex) { this.chosenIndex = chosenIndex; }
}



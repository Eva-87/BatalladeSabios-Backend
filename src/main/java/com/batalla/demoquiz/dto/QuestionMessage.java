package com.batalla.demoquiz.dto;

import java.util.List;

public class QuestionMessage {

    private Long questionId;
    private String text;
    private List<String> options;
    private int round;
    private int correctIndex;
    private String explanation;

    public QuestionMessage(Long questionId, String text, List<String> options, int round, int correctIndex, String explanation) {
        this.questionId = questionId;
        this.text = text;
        this.options = options;
        this.round = round;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getRound() {
        return round;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public String getExplanation() {
        return explanation;
    }
}

package com.batalla.demoquiz.dto;

import java.util.List;

public class QuestionMessage {
    private Long questionId;
    private String text;
    private List<String> options;
    private int round;

    public QuestionMessage() {}

    public QuestionMessage(Long questionId, String text, List<String> options, int round) {
        this.questionId = questionId;
        this.text = text;
        this.options = options;
        this.round = round;
    }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }
}

    

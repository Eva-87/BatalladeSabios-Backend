package com.batalla.demoquiz.dto;

public record QuestionDTO(
    Long id,
    String text,
    String optionA,
    String optionB,
    String optionC,
    String optionD,
    int correctIndex
) {}

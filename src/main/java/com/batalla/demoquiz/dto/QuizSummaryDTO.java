package com.batalla.demoquiz.dto;

public record QuizSummaryDTO(
    Long id,
    String title,
    String topic,
    String imageUrl
) {}

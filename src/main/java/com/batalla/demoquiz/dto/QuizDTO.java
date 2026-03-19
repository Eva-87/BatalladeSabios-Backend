package com.batalla.demoquiz.dto;

import java.util.List;

public record QuizDTO(
        Long id,
        String title,
        String topic,
        String imageUrl,
        List<QuestionDTO> questions
) {}

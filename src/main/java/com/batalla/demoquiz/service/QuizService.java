package com.batalla.demoquiz.service;

import com.batalla.demoquiz.dto.QuizDTO;
import com.batalla.demoquiz.dto.QuizUpdateRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QuizService {

    QuizDTO createQuiz(
            String title,
            String topic,
            Long userId,
            boolean isCustomTopic,
            List<Long> questionIds,
            String imageUrl
    );

    QuizDTO getById(Long id);

    List<QuizDTO> getAll();

    ResponseEntity<?> updateQuiz(Long quizId, QuizUpdateRequest request);
}

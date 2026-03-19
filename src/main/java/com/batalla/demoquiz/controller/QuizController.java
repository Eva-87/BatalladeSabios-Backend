package com.batalla.demoquiz.controller;

import com.batalla.demoquiz.dto.QuizDTO;
import com.batalla.demoquiz.dto.QuizSummaryDTO;
import com.batalla.demoquiz.dto.QuizUpdateRequest;
import com.batalla.demoquiz.entity.Quiz;
import com.batalla.demoquiz.repository.QuizRepository;
import com.batalla.demoquiz.service.QuizService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "http://localhost:5173")
public class QuizController {

    private final QuizService quizService;
    private final QuizRepository quizRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public QuizController(QuizService quizService, QuizRepository quizRepository) {
        this.quizService = quizService;
        this.quizRepository = quizRepository;
    }

    @PostMapping(
            value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public QuizDTO createQuiz(
            @RequestParam("title") String title,
            @RequestParam("topic") String topic,
            @RequestParam("userId") Long userId,
            @RequestParam("isCustomTopic") boolean isCustomTopic,
            @RequestParam("questionIds") String questionIdsJson,
            @RequestParam(value = "imageUrl", required = false) String imageUrl
    ) throws Exception {

        List<Long> questionIds = mapper.readValue(
                questionIdsJson,
                new TypeReference<List<Long>>() {}
        );

        return quizService.createQuiz(
                title,
                topic,
                userId,
                isCustomTopic,
                questionIds,
                imageUrl
        );
    }

    @GetMapping
    public List<QuizSummaryDTO> getAll() {
        return quizRepository.findAll().stream()
                .map(q -> new QuizSummaryDTO(
                        q.getId(),
                        q.getTitle(),
                        q.getTopic() != null ? q.getTopic().name() : "CUSTOM",
                        q.getImageUrl()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public QuizDTO getQuizById(@PathVariable Long id) {
        return quizService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<QuizSummaryDTO> getByUser(@PathVariable Long userId) {
        return quizRepository.findByCreatorId(userId).stream()
                .map(q -> new QuizSummaryDTO(
                        q.getId(),
                        q.getTitle(),
                        q.getTopic() != null ? q.getTopic().name() : "CUSTOM",
                        q.getImageUrl()
                ))
                .toList();
    }

    @PutMapping("/update/{quizId}")
    public ResponseEntity<?> updateQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizUpdateRequest request
    ) {
        return quizService.updateQuiz(quizId, request);
    }

    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        quizRepository.deleteById(id);
    }
}

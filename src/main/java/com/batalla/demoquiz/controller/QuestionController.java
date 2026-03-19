package com.batalla.demoquiz.controller;

import com.batalla.demoquiz.dto.QuestionDTO;
import com.batalla.demoquiz.entity.Question;
import com.batalla.demoquiz.enums.Topic;
import com.batalla.demoquiz.enums.Difficulty;
import com.batalla.demoquiz.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<QuestionDTO> getAll() {
        return questionService.getAllQuestions();
    }

    @PostMapping("/create/{creatorId}")
    public Question create(@PathVariable Long creatorId, @RequestBody Question q) {
        return questionService.createQuestion(creatorId, q);
    }

    @GetMapping("/filter")
    public List<QuestionDTO> byTopicAndDifficulty(
            @RequestParam Topic topic,
            @RequestParam Difficulty difficulty
    ) {
        return questionService.getByTopicAndDifficulty(topic, difficulty);
    }
}

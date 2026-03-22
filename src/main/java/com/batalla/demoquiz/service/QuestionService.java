package com.batalla.demoquiz.service;

import com.batalla.demoquiz.dto.QuestionDTO;
import com.batalla.demoquiz.entity.Question;
import com.batalla.demoquiz.enums.Topic;
import com.batalla.demoquiz.enums.Difficulty;

import java.util.List;

public interface QuestionService {

    List<QuestionDTO> getAllQuestions();

    Question createQuestion(Long creatorId, Question q);

    List<QuestionDTO> getByTopicAndDifficulty(Topic topic, Difficulty difficulty);
}

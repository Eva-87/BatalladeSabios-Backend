package com.batalla.demoquiz.service;

import com.batalla.demoquiz.dto.QuestionDTO;  // ← ESTE IMPORT ES LO QUE FALTABA
import com.batalla.demoquiz.entity.Question;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.enums.Topic;
import com.batalla.demoquiz.enums.Difficulty;
import com.batalla.demoquiz.repository.QuestionRepository;
import com.batalla.demoquiz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<QuestionDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(q -> new QuestionDTO(
                        q.getId(),
                        q.getText(),
                        q.getOptionA(),
                        q.getOptionB(),
                        q.getOptionC(),
                        q.getOptionD(),
                        q.getCorrectIndex()
                ))
                .toList();
    }

    @Override
    public Question createQuestion(Long creatorId, Question q) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        q.setCreator(creator);
        return questionRepository.save(q);
    }

    @Override
    public List<QuestionDTO> getByTopicAndDifficulty(Topic topic, Difficulty difficulty) {
        return questionRepository.findByTopicAndDifficulty(topic, difficulty)
                .stream()
                .map(q -> new QuestionDTO(
                        q.getId(),
                        q.getText(),
                        q.getOptionA(),
                        q.getOptionB(),
                        q.getOptionC(),
                        q.getOptionD(),
                        q.getCorrectIndex()
                ))
                .toList();
    }
}

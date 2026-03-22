package com.batalla.demoquiz.repository;

import com.batalla.demoquiz.entity.Question;
import com.batalla.demoquiz.enums.Difficulty;
import com.batalla.demoquiz.enums.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuiz_TopicAndDifficulty(Topic topic, Difficulty difficulty);
}

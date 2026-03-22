package com.batalla.demoquiz.service;

import com.batalla.demoquiz.dto.QuestionDTO;
import com.batalla.demoquiz.dto.QuizDTO;
import com.batalla.demoquiz.dto.QuizUpdateRequest;
import com.batalla.demoquiz.entity.CustomTopic;
import com.batalla.demoquiz.entity.Question;
import com.batalla.demoquiz.entity.Quiz;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.enums.Topic;
import com.batalla.demoquiz.repository.CustomTopicRepository;
import com.batalla.demoquiz.repository.QuestionRepository;
import com.batalla.demoquiz.repository.QuizRepository;
import com.batalla.demoquiz.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CustomTopicRepository customTopicRepository;

    public QuizServiceImpl(
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            UserRepository userRepository,
            CustomTopicRepository customTopicRepository
    ) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.customTopicRepository = customTopicRepository;
    }

    @Override
    public QuizDTO createQuiz(
            String title,
            String topic,
            Long userId,
            boolean isCustomTopic,
            List<Long> questionIds,
            String imageUrl
    ) {
        Quiz quiz = new Quiz();
        quiz.setTitle(title);

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        quiz.setCreator(creator);

        if (isCustomTopic) {
            CustomTopic custom = customTopicRepository.findByName(topic)
                    .orElseThrow(() -> new RuntimeException("Custom topic not found"));
            quiz.setCustomTopicId(custom.getId());
        } else {
            quiz.setTopic(Topic.valueOf(topic));
        }

        List<Question> questions = questionRepository.findAllById(questionIds);
        questions.forEach(q -> q.setQuiz(quiz));
        quiz.setQuestions(questions);

        quiz.setImageUrl(imageUrl);

        Quiz saved = quizRepository.save(quiz);

        return toDTO(saved);
    }

    @Override
    public QuizDTO getById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz no encontrado"));
        return toDTO(quiz);
    }

    @Override
    public List<QuizDTO> getAll() {
        return quizRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
public ResponseEntity<?> updateQuiz(Long quizId, QuizUpdateRequest request) {

    Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz no encontrado"));

    // -----------------------------
    // TÍTULO
    // -----------------------------
    if (request.getTitle() != null) {
        quiz.setTitle(request.getTitle());
    }

    // -----------------------------
    // TOPIC (NORMAL O CUSTOM)
    // -----------------------------
    if (request.getTopic() != null) {

        // Si es un topic del enum
        try {
            Topic enumTopic = Topic.valueOf(request.getTopic());
            quiz.setTopic(enumTopic);
            quiz.setCustomTopicId(null); // limpiar custom
        } catch (IllegalArgumentException e) {

            // Si NO es enum → es custom topic
            CustomTopic custom = customTopicRepository.findByName(request.getTopic())
                    .orElseThrow(() -> new RuntimeException("Custom topic not found"));

            quiz.setTopic(null);
            quiz.setCustomTopicId(custom.getId());
        }
    }

    // -----------------------------
    // IMAGEN
    // -----------------------------
    if (request.getImageUrl() != null) {
        quiz.setImageUrl(request.getImageUrl());
    }

    // -----------------------------
    // PREGUNTAS
    // -----------------------------
    if (request.getQuestionIds() != null) {

        List<Question> newQuestions =
                questionRepository.findAllById(request.getQuestionIds());

        // Quitar relación anterior
        if (quiz.getQuestions() != null) {
            quiz.getQuestions().forEach(q -> q.setQuiz(null));
        }

        // Asignar nuevas
        newQuestions.forEach(q -> q.setQuiz(quiz));

        quiz.setQuestions(newQuestions);
    }

    quizRepository.save(quiz);

    return ResponseEntity.ok("Quiz actualizado correctamente");
}

    private QuizDTO toDTO(Quiz quiz) {

    List<QuestionDTO> questionDTOs = quiz.getQuestions().stream()
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

    String topicStr;

    if (quiz.getTopic() != null) {
        // Topic normal (enum)
        topicStr = quiz.getTopic().name();
    } else {
        // Topic personalizado
        CustomTopic custom = customTopicRepository.findById(quiz.getCustomTopicId())
                .orElse(null);

        topicStr = custom != null ? custom.getName() : "CUSTOM";
    }

    return new QuizDTO(
            quiz.getId(),
            quiz.getTitle(),
            topicStr,
            quiz.getImageUrl(),
            questionDTOs
    );
}

}

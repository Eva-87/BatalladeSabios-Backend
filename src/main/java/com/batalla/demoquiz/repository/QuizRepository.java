package com.batalla.demoquiz.repository;

import com.batalla.demoquiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // ⭐ Buscar por el ID del usuario creador
    List<Quiz> findByCreatorId(Long userId);
}


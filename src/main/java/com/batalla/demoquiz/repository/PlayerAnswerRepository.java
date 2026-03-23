package com.batalla.demoquiz.repository;

import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.PlayerAnswer;
import com.batalla.demoquiz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerAnswerRepository extends JpaRepository<PlayerAnswer, Long> {

    // Obtener todas las respuestas de una pregunta en una sala
    List<PlayerAnswer> findByRoomAndQuestionIndex(GameRoom room, int questionIndex);

    // Obtener todas las respuestas de un jugador en una sala
    List<PlayerAnswer> findByRoomAndUser(GameRoom room, User user);

    // Saber si un jugador ya respondió una pregunta
    boolean existsByRoomAndUserAndQuestionIndex(GameRoom room, User user, int questionIndex);

    // ⭐⭐ EL MÉTODO QUE PEDISTE ⭐⭐
    void deleteByRoomAndQuestionIndex(GameRoom room, int questionIndex);
}

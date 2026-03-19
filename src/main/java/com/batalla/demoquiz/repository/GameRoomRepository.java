package com.batalla.demoquiz.repository;

import com.batalla.demoquiz.entity.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    Optional<GameRoom> findByCode(String code);
}

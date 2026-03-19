package com.batalla.demoquiz.repository;

import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomPlayerRepository extends JpaRepository<RoomPlayer, Long> {
    List<RoomPlayer> findByRoom(GameRoom room);
}

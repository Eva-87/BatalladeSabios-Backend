package com.batalla.demoquiz.service;

import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.RoomPlayer;

import java.util.List;

public interface GameRoomService {

    GameRoom createRoom(Long creatorId, Long quizId, int maxPlayers);

    RoomPlayer joinRoom(String code, Long userId);

    List<RoomPlayer> getPlayers(String code);

    GameRoom getRoom(String code);
}

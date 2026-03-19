package com.batalla.demoquiz.service;

import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.enums.GameStatus;

public interface RoomService {

    GameRoom createRoom(Long quizId, Long userId, int maxPlayers);

    GameRoom getRoomByCode(String code);

    GameStatus getStatus(String code);

}

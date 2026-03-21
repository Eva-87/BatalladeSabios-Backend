package com.batalla.demoquiz.dto;

public record RoomDTO(
        String code,
        String quizTitle,
        Long creatorId,
        int maxPlayers,
        String status
) {}

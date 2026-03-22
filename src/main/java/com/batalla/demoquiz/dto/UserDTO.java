package com.batalla.demoquiz.dto;


public record UserDTO(
        Long id,
        String username,
        int totalScore,
        String avatarUrl,
        String role
) {}

package com.batalla.demoquiz.dto;

import java.util.List;

public class GameOverMessage {

    private List<PlayerResult> ranking;

    public GameOverMessage(List<PlayerResult> ranking) {
        this.ranking = ranking;
    }

    public List<PlayerResult> getRanking() {
        return ranking;
    }

    public static class PlayerResult {
        private Long userId;
        private String username;
        private int score;
        private int correctAnswers;
        private int totalQuestions;

        public PlayerResult(Long userId, String username, int score, int correctAnswers, int totalQuestions) {
            this.userId = userId;
            this.username = username;
            this.score = score;
            this.correctAnswers = correctAnswers;
            this.totalQuestions = totalQuestions;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public int getScore() {
            return score;
        }

        public int getCorrectAnswers() {
            return correctAnswers;
        }

        public int getTotalQuestions() {
            return totalQuestions;
        }
    }
}




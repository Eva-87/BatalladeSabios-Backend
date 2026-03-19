package com.batalla.demoquiz.dto;

import java.util.List;

public class GameOverMessage {

    public static class PlayerResult {
        private Long userId;
        private String username;
        private int score;

        public PlayerResult() {}

        public PlayerResult(Long userId, String username, int score) {
            this.userId = userId;
            this.username = username;
            this.score = score;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
    }

    private List<PlayerResult> ranking;

    public GameOverMessage() {}

    public GameOverMessage(List<PlayerResult> ranking) {
        this.ranking = ranking;
    }

    public List<PlayerResult> getRanking() { return ranking; }
    public void setRanking(List<PlayerResult> ranking) { this.ranking = ranking; }
}

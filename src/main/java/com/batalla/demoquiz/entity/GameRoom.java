package com.batalla.demoquiz.entity;

import com.batalla.demoquiz.enums.GameStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class GameRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private int maxPlayers;

    @Transient
    private Map<Long, Integer> correctAnswers = new HashMap<>();

    @Transient
    private Map<Long, Integer> wrongAnswers = new HashMap<>();

    @ManyToOne
    private User creator;

    @ManyToOne
    private Quiz quiz;

    @OneToMany(mappedBy = "room")
    @JsonManagedReference
    private List<RoomPlayer> players;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.LOBBY;

    private int currentQuestionIndex = 0;
    private boolean gameFinished = false;

    public void addCorrect(Long userId) {
        correctAnswers.put(userId, correctAnswers.getOrDefault(userId, 0) + 1);
    }

    public void addWrong(Long userId) {
        wrongAnswers.put(userId, wrongAnswers.getOrDefault(userId, 0) + 1);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public List<RoomPlayer> getPlayers() { return players; }
    public void setPlayers(List<RoomPlayer> players) { this.players = players; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public void setCurrentQuestionIndex(int currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }

    public boolean isGameFinished() { return gameFinished; }
    public void setGameFinished(boolean gameFinished) { this.gameFinished = gameFinished; }

    public Map<Long, Integer> getCorrectAnswers() {
        return correctAnswers;
    }

    public Map<Long, Integer> getWrongAnswers() {
        return wrongAnswers;
    }
}

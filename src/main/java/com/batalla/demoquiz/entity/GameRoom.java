package com.batalla.demoquiz.entity;

import com.batalla.demoquiz.dto.RoundResultMessage;
import com.batalla.demoquiz.enums.GameStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class GameRoom {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private int maxPlayers;

    @Column(name = "last_round_result_json")
    private String lastRoundResultJson;

    @Transient
    private RoundResultMessage lastRoundResult;

    @Transient
    private Map<Integer, Set<Long>> answersPerRound = new HashMap<>();

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

    public Map<Long, Integer> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Map<Long, Integer> map) { this.correctAnswers = map; }

    public Map<Long, Integer> getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(Map<Long, Integer> map) { this.wrongAnswers = map; }

    public Map<Integer, Set<Long>> getAnswersPerRound() { return answersPerRound; }
    public void setAnswersPerRound(Map<Integer, Set<Long>> map) { this.answersPerRound = map; }

    public Long getId() { return id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public RoundResultMessage getLastRoundResult() {
        if (lastRoundResult != null) return lastRoundResult;
        if (lastRoundResultJson == null) return null;
        try {
            this.lastRoundResult = MAPPER.readValue(lastRoundResultJson, RoundResultMessage.class);
            return this.lastRoundResult;
        } catch (Exception e) {
            return null;
        }
    }

    public void setLastRoundResult(RoundResultMessage lastRoundResult) {
        this.lastRoundResult = lastRoundResult;
        if (lastRoundResult == null) {
            this.lastRoundResultJson = null;
        } else {
            try {
                this.lastRoundResultJson = MAPPER.writeValueAsString(lastRoundResult);
            } catch (JsonProcessingException e) {
                this.lastRoundResultJson = null;
            }
        }
    }

    public String getLastRoundResultJson() { return lastRoundResultJson; }
    public void setLastRoundResultJson(String json) { this.lastRoundResultJson = json; }

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
}

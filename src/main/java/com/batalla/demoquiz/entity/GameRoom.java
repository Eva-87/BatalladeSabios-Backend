package com.batalla.demoquiz.entity;

import com.batalla.demoquiz.enums.GameStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class GameRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private int maxPlayers;

    @ManyToOne
    private User creator;

    @ManyToOne
    private Quiz quiz;

    @OneToMany(mappedBy = "room")
    @JsonManagedReference // ⭐ evita ciclos
    private List<RoomPlayer> players;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.LOBBY;

    // getters y setters...


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
}

package com.batalla.demoquiz.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class RoomPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference // ⭐ evita ciclo con GameRoom
    private GameRoom room;

    @ManyToOne
    private User user;

    private int score;

    private boolean connected;

    // getters y setters...

    public RoomPlayer() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GameRoom getRoom() { return room; }
    public void setRoom(GameRoom room) { this.room = room; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }
}

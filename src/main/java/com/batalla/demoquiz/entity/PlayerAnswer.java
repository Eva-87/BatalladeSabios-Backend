package com.batalla.demoquiz.entity;

import jakarta.persistence.*;

@Entity
public class PlayerAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private GameRoom room;

    @ManyToOne
    private User user;

    private int questionIndex;

    private boolean correct;

    // -------------------------
    // CONSTRUCTOR VACÍO (JPA)
    // -------------------------
    public PlayerAnswer() {}

    // -------------------------
    // CONSTRUCTOR ÚTIL
    // -------------------------
    public PlayerAnswer(GameRoom room, User user, int questionIndex, boolean correct) {
        this.room = room;
        this.user = user;
        this.questionIndex = questionIndex;
        this.correct = correct;
    }

    // -------------------------
    // GETTERS / SETTERS
    // -------------------------
    public Long getId() { return id; }

    public GameRoom getRoom() { return room; }
    public void setRoom(GameRoom room) { this.room = room; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getQuestionIndex() { return questionIndex; }
    public void setQuestionIndex(int questionIndex) { this.questionIndex = questionIndex; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}

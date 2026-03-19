package com.batalla.demoquiz.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_topics")
public class CustomTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    public CustomTopic() {}

    public CustomTopic(String name, User creator) {
        this.name = name;
        this.creator = creator;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
}

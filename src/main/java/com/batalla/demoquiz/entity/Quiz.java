package com.batalla.demoquiz.entity;

import com.batalla.demoquiz.enums.Topic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Topic topic;

    private Long customTopicId;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonIgnoreProperties({"quizzes"})
    private User creator;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Question> questions;

    public Quiz() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }

    public Long getCustomTopicId() { return customTopicId; }
    public void setCustomTopicId(Long customTopicId) { this.customTopicId = customTopicId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}

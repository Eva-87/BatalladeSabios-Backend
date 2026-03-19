package com.batalla.demoquiz.repository;

import com.batalla.demoquiz.entity.CustomTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomTopicRepository extends JpaRepository<CustomTopic, Long> {
    Optional<CustomTopic> findByName(String name);
}

package com.batalla.demoquiz.controller;

import com.batalla.demoquiz.entity.CustomTopic;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.repository.CustomTopicRepository;
import com.batalla.demoquiz.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/custom-topics")
public class CustomTopicController {

    private final CustomTopicRepository customTopicRepository;
    private final UserRepository userRepository;

    public CustomTopicController(CustomTopicRepository customTopicRepository,
                                 UserRepository userRepository) {
        this.customTopicRepository = customTopicRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create/{userId}")
    public CustomTopic create(@PathVariable Long userId, @RequestBody String name) {

        String cleanName = name.replace("\"", "").trim();

        if (cleanName.isEmpty()) {
            throw new RuntimeException("Topic name cannot be empty");
        }

        // Validar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validar duplicado
        customTopicRepository.findByName(cleanName)
                .ifPresent(t -> {
                    throw new RuntimeException("Topic already exists");
                });

        // Crear tópico
        CustomTopic topic = new CustomTopic(cleanName, user);

        return customTopicRepository.save(topic);
    }

    @GetMapping
    public List<CustomTopic> getAll() {
        return customTopicRepository.findAll();
    }
}

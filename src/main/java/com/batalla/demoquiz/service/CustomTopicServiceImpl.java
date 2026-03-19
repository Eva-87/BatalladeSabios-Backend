package com.batalla.demoquiz.service;

import com.batalla.demoquiz.entity.CustomTopic;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.repository.CustomTopicRepository;
import com.batalla.demoquiz.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomTopicServiceImpl implements CustomTopicService {

    private final CustomTopicRepository customTopicRepository;
    private final UserRepository userRepository;

    public CustomTopicServiceImpl(CustomTopicRepository customTopicRepository, UserRepository userRepository) {
        this.customTopicRepository = customTopicRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CustomTopic createCustomTopic(Long userId, String name) {

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        customTopicRepository.findByName(name).ifPresent(t -> {
            throw new RuntimeException("Topic already exists");
        });

        CustomTopic topic = new CustomTopic();
        topic.setName(name);
        topic.setCreator(creator);

        return customTopicRepository.save(topic);
    }
}

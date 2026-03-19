package com.batalla.demoquiz.service;


import com.batalla.demoquiz.entity.CustomTopic;

public interface CustomTopicService {
    CustomTopic createCustomTopic(Long userId, String name);
}

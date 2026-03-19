package com.batalla.demoquiz.service;

import com.batalla.demoquiz.entity.User;

import java.util.List;

public interface UserService {

    User register(String username, String email, String password);

    User login(String email, String password);

    void addScore(Long userId, int points);

    List<User> getGlobalRanking();

    User createUser(User user);


}



package com.batalla.demoquiz.controller;

import com.batalla.demoquiz.dto.RegisterRequest;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request.getUsername(), request.getEmail(), request.getPassword());
    }

    @GetMapping("/ranking")
    public List<User> globalRanking() {
        return userService.getGlobalRanking();
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}



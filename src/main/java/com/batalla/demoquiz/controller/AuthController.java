package com.batalla.demoquiz.controller;

import com.batalla.demoquiz.dto.LoginRequest;
import com.batalla.demoquiz.dto.RegisterRequest;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.security.JwtUtil;
import com.batalla.demoquiz.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // -----------------------------
    // REGISTRO (CON EMAIL)
    // -----------------------------
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
    }

    // -----------------------------
    // LOGIN (CON USERNAME)
    // -----------------------------
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {

        User user = userService.login(
                request.getUsername(),   // ← CAMBIADO
                request.getPassword()
        );

        String token = jwtUtil.generateToken(user.getUsername()); // ← CAMBIADO

        return Map.of(
                "token", token,
                "user", user
        );
    }
}

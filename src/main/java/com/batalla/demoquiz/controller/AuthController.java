package com.batalla.demoquiz.controller;

import com.batalla.demoquiz.dto.LoginRequest;
import com.batalla.demoquiz.dto.RegisterRequest;
import com.batalla.demoquiz.dto.UserDTO;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // -----------------------------
    // REGISTRO
    // -----------------------------
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole() // USER o CREATOR
        );
    }

    // -----------------------------
    // LOGIN
    // -----------------------------
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {

    User user = userService.login(
            request.getUsername(),
            request.getPassword()
    );

    UserDTO dto = new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getTotalScore(),
            user.getAvatarUrl(),
            user.getRole()
    );

    return Map.of(
            "message", "Login correcto",
            "user", dto
    );
}

}




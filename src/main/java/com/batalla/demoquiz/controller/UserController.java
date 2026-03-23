package com.batalla.demoquiz.controller;

import com.batalla.demoquiz.dto.RegisterRequest;
import com.batalla.demoquiz.dto.UserDTO;
import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // 👈 FALTABA ESTE IMPORT

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
    }

    @GetMapping("/ranking")
    public List<User> globalRanking() {
        return userService.getGlobalRanking();
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}/avatar")
    public UserDTO updateAvatar(@PathVariable Long id, @RequestBody Map<String, String> body) {

        String avatarUrl = body.get("avatarUrl");

        User user = userService.updateAvatar(id, avatarUrl);

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getTotalScore(),
                user.getAvatarUrl(),
                user.getRole()
        );
    }
@PutMapping("/{id}/add-win")
public UserDTO addWin(@PathVariable Long id) {
    User user = userService.addWin(id);

    return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getTotalScore(),
            user.getAvatarUrl(),
            user.getRole()
    );
}

}

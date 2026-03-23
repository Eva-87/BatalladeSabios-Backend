package com.batalla.demoquiz.service;

import com.batalla.demoquiz.entity.User;
import com.batalla.demoquiz.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public User register(String username, String email, String password, String role) {

        // Evitar duplicados
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new RuntimeException("Email already registered");
        });

        userRepository.findByUsername(username).ifPresent(u -> {
            throw new RuntimeException("Username already taken");
        });

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));
        u.setRole(role); // USER o CREATOR
        u.setTotalScore(0);
        u.setWins(0); // ⭐ Inicializamos victorias

        return userRepository.save(u);
    }

    @Override
    public User login(String username, String password) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found"));

        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        return u;
    }

    @Override
    public void addScore(Long userId, int points) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        u.setTotalScore(u.getTotalScore() + points);
        userRepository.save(u);
    }

    @Override
    public List<User> getGlobalRanking() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "totalScore"));
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateAvatar(Long id, String avatarUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }

    // ⭐⭐⭐ MÉTODO NUEVO: SUMAR UNA VICTORIA ⭐⭐⭐
    @Override
    public User addWin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setWins(user.getWins() + 1);

        return userRepository.save(user);
    }
}

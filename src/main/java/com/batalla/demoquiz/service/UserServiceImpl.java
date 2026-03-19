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
    public User register(String username, String email, String password) {

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
        u.setRole("USER");
        u.setTotalScore(0);

        return userRepository.save(u);
    }

    @Override
    public User login(String email, String password) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

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
}

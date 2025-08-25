package com.vesco.financetracker.service;

import com.vesco.financetracker.entity.User;
import com.vesco.financetracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return optionalUser.get();
    }

    public User updateProfile(Long userId, com.vesco.financetracker.dto.UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.vesco.financetracker.exception.ResourceNotFoundException("User not found"));

        if (req.name() != null && !req.name().isBlank())
            user.setName(req.name());
        if (req.email() != null && !req.email().isBlank()) {
            // check uniqueness
            if (!user.getEmail().equals(req.email()) && userRepository.existsByEmail(req.email())) {
                throw new com.vesco.financetracker.exception.BadRequestException("Email already in use");
            }
            user.setEmail(req.email());
        }
        if (req.goalAmount() != null)
            user.setGoalAmount(req.goalAmount());
        if (req.password() != null && !req.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(req.password()));
        }

        return userRepository.save(user);
    }

    public java.util.Map<String, String> generateTokenAndCookie(User user,
            com.vesco.financetracker.util.JwtUtil jwtUtil, boolean secure, String sameSite) {
        String token = jwtUtil.generateToken(user.getId());
        long maxAge = jwtUtil.getExpirationMs() / 1000;
        String cookie = com.vesco.financetracker.util.CookieUtil.authSetCookie(token, maxAge, secure, sameSite);
        return java.util.Map.of("token", token, "cookie", cookie);
    }
}

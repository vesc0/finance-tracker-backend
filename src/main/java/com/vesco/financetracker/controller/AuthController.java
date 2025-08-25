package com.vesco.financetracker.controller;

import com.vesco.financetracker.dto.*;
import com.vesco.financetracker.entity.User;
import com.vesco.financetracker.service.AuthService;
import com.vesco.financetracker.util.JwtUtil;
import com.vesco.financetracker.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final com.vesco.financetracker.repository.UserRepository userRepository;

    public AuthController(AuthService authService, JwtUtil jwtUtil,
            com.vesco.financetracker.repository.UserRepository userRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(request.password());

        User savedUser = authService.register(user);
        var pair = authService.generateTokenAndCookie(savedUser, jwtUtil, false, null);
        return ResponseEntity.ok().header("Set-Cookie", pair.get("cookie"))
                .body(new AuthResponse(pair.get("token"), savedUser.getId(), savedUser.getName(), savedUser.getEmail(),
                        savedUser.getGoalAmount()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.email(), request.password());
        var pair = authService.generateTokenAndCookie(user, jwtUtil, false, null);
        return ResponseEntity.ok().header("Set-Cookie", pair.get("cookie"))
                .body(new AuthResponse(pair.get("token"), user.getId(), user.getName(), user.getEmail(),
                        user.getGoalAmount()));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile(
            @AuthenticationPrincipal com.vesco.financetracker.security.AppUserPrincipal principal) {
        if (principal == null)
            return ResponseEntity.status(401).build();
        User user = userRepository.findById(principal.getId()).orElse(null);
        if (user == null)
            return ResponseEntity.status(404).build();
        return ResponseEntity
                .ok(new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getGoalAmount()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal com.vesco.financetracker.security.AppUserPrincipal principal,
            @RequestBody UpdateProfileRequest req) {
        if (principal == null)
            return ResponseEntity.status(401).build();
        User updated = authService.updateProfile(principal.getId(), req);
        return ResponseEntity.ok(java.util.Map.of("message", "Profile updated", "name", updated.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Clear the cookie
        return ResponseEntity.ok().header("Set-Cookie", CookieUtil.clearAuthCookie())
                .body(java.util.Map.of("message", "Logged out"));
    }

}

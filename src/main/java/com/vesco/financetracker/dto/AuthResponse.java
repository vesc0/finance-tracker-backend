package com.vesco.financetracker.dto;

public record AuthResponse(String token, Long id, String name, String email, Double goalAmount) {
}

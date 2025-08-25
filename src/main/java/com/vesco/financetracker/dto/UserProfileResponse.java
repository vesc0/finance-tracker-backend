package com.vesco.financetracker.dto;

public record UserProfileResponse(Long id, String name, String email, Double goalAmount) {
}

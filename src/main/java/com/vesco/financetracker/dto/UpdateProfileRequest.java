package com.vesco.financetracker.dto;

public record UpdateProfileRequest(String name, String email, String password, Double goalAmount) {
}

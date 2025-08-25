package com.vesco.financetracker.dto;

import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String type,
        String category,
        Double amount,
        String method,
        String status,
        String note,
        LocalDateTime date,
        LocalDateTime createdAt,
        Long userId) {
}

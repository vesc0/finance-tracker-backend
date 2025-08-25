package com.vesco.financetracker.dto;

import com.vesco.financetracker.entity.Transaction;

public class TransactionMapper {
    public static TransactionResponse toDto(Transaction t) {
        Long userId = t.getUser() != null ? t.getUser().getId() : null;
        return new TransactionResponse(
                t.getId(),
                t.getType(),
                t.getCategory(),
                t.getAmount(),
                t.getMethod(),
                t.getStatus(),
                t.getNote(),
                t.getDate(),
                t.getCreatedAt(),
                userId);
    }
}

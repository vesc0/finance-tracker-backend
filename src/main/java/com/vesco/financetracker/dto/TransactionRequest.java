package com.vesco.financetracker.dto;

import java.time.OffsetDateTime;

public record TransactionRequest(
                String type,
                String category,
                Double amount,
                String method,
                String status,
                String note,
                OffsetDateTime date) {
}

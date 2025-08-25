package com.vesco.financetracker.dto;

public record AnalyticsSummary(
        Double totalIncome,
        Double totalExpenses,
        Double totalDue,
        Double netWorth,
        Double goalAmount) {
}

package com.vesco.financetracker.dto;

public record MonthlyReportEntry(Integer year, Integer month, String monthName, String type, Double totalAmount) {
}

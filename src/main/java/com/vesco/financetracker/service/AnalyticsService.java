package com.vesco.financetracker.service;

import com.vesco.financetracker.dto.AnalyticsSummary;
import com.vesco.financetracker.dto.CategoryReportEntry;
import com.vesco.financetracker.dto.MonthlyReportEntry;
import com.vesco.financetracker.repository.TransactionRepository;
import com.vesco.financetracker.repository.UserRepository;
import com.vesco.financetracker.entity.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AnalyticsService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public AnalyticsSummary getSummary(Long userId) {
        double totalIncome = Optional
                .ofNullable(transactionRepository.sumAmountByUserIdAndTypeAndStatus(userId, "income", "completed"))
                .orElse(0.0);
        double totalExpenses = Optional
                .ofNullable(transactionRepository.sumAmountByUserIdAndTypeAndStatus(userId, "expense", "completed"))
                .orElse(0.0);
        double netWorth = totalIncome - totalExpenses;

        double awaitingIncome = Optional
                .ofNullable(transactionRepository.sumAmountByUserIdAndTypeAndStatus(userId, "income", "awaiting"))
                .orElse(0.0);
        double awaitingExpense = Optional
                .ofNullable(transactionRepository.sumAmountByUserIdAndTypeAndStatus(userId, "expense", "awaiting"))
                .orElse(0.0);
        double totalDue = awaitingIncome - awaitingExpense;

        double goalAmount = userRepository.findById(userId).map(u -> u.getGoalAmount()).orElse(0.0);

        return new AnalyticsSummary(totalIncome, totalExpenses, totalDue, netWorth, goalAmount);
    }

    public List<MonthlyReportEntry> getMonthlyReport(Long userId, int monthsBack) {
        LocalDateTime from = LocalDateTime.now(ZoneOffset.UTC).minusMonths(monthsBack);
        List<Transaction> txs = transactionRepository.findByUserIdAndDateAfterAndStatusAndTypeIn(userId, from,
                "completed", Arrays.asList("income", "expense"));

        Map<String, Double> grouped = txs.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDate().getYear() + "-" + t.getDate().getMonthValue() + "-" + t.getType(),
                        Collectors.summingDouble(Transaction::getAmount)));

        List<MonthlyReportEntry> out = new ArrayList<>();
        for (var e : grouped.entrySet()) {
            String[] parts = e.getKey().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            String type = parts[2];
            String monthName = java.time.Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            out.add(new MonthlyReportEntry(year, month, monthName, type, e.getValue()));
        }

        out.sort(Comparator.comparing(MonthlyReportEntry::year).thenComparing(MonthlyReportEntry::month));
        return out;
    }

    public List<CategoryReportEntry> getCategoryReport(Long userId) {
        List<Transaction> txs = transactionRepository.findByUserIdAndTypeAndStatus(userId, "expense", "completed");
        Map<String, Double> grouped = txs.stream().collect(
                Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
        return grouped.entrySet().stream().map(e -> new CategoryReportEntry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}

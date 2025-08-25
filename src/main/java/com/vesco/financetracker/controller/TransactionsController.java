package com.vesco.financetracker.controller;

import com.vesco.financetracker.dto.TransactionRequest;
import com.vesco.financetracker.entity.Transaction;
import com.vesco.financetracker.service.TransactionService;
import com.vesco.financetracker.dto.TransactionMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {

    private final TransactionService transactionService;

    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<com.vesco.financetracker.dto.TransactionResponse> addTransaction(
            @RequestBody TransactionRequest request,
            @AuthenticationPrincipal com.vesco.financetracker.security.AppUserPrincipal principal) {

        if (principal == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing authentication");
        Long userId = principal.getId();
        Transaction savedTransaction = transactionService.addTransaction(userId, request);
        return ResponseEntity.ok(TransactionMapper.toDto(savedTransaction));
    }

    @GetMapping
    public ResponseEntity<List<com.vesco.financetracker.dto.TransactionResponse>> getUserTransactions(
            @AuthenticationPrincipal com.vesco.financetracker.security.AppUserPrincipal principal) {
        if (principal == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing authentication");
        Long userId = principal.getId();
        List<Transaction> transactions = transactionService.getUserTransactions(userId);
        List<com.vesco.financetracker.dto.TransactionResponse> dtos = transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.vesco.financetracker.dto.TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionRequest request,
            @AuthenticationPrincipal com.vesco.financetracker.security.AppUserPrincipal principal) {
        if (principal == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing authentication");
        Transaction updatedTransaction = transactionService.updateTransaction(principal.getId(), id, request);
        return ResponseEntity.ok(TransactionMapper.toDto(updatedTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal com.vesco.financetracker.security.AppUserPrincipal principal) {
        if (principal == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing authentication");
        transactionService.deleteTransaction(principal.getId(), id);
        return ResponseEntity.ok("Transaction deleted successfully");
    }
}

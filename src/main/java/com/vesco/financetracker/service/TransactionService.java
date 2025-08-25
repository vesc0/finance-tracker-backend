package com.vesco.financetracker.service;

import com.vesco.financetracker.dto.TransactionRequest;
import com.vesco.financetracker.entity.Transaction;
import com.vesco.financetracker.entity.User;
import com.vesco.financetracker.exception.ResourceNotFoundException;
import com.vesco.financetracker.exception.ResourceForbiddenException;
import com.vesco.financetracker.repository.TransactionRepository;
import com.vesco.financetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Transaction addTransaction(Long userId, TransactionRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Transaction transaction = mapRequestToEntity(request);
        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Transaction updateTransaction(Long userId, Long txId, TransactionRequest request) {
        Transaction existing = transactionRepository.findById(txId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (existing.getUser() == null || !existing.getUser().getId().equals(userId)) {
            throw new ResourceForbiddenException("Not authorized to update this transaction");
        }
        existing.setType(request.type());
        existing.setCategory(request.category());
        existing.setAmount(request.amount());
        existing.setMethod(request.method());
        existing.setStatus(request.status());
        existing.setNote(request.note());
        OffsetDateTime odt = request.date();
        if (odt != null)
            existing.setDate(odt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
        return transactionRepository.save(existing);
    }

    public void deleteTransaction(Long userId, Long txId) {
        Transaction existing = transactionRepository.findById(txId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (existing.getUser() == null || !existing.getUser().getId().equals(userId)) {
            throw new ResourceForbiddenException("Not authorized to delete this transaction");
        }
        transactionRepository.deleteById(txId);
    }

    private Transaction mapRequestToEntity(TransactionRequest request) {
        Transaction t = new Transaction();
        t.setType(request.type());
        t.setCategory(request.category());
        t.setAmount(request.amount());
        t.setMethod(request.method());
        t.setStatus(request.status());
        t.setNote(request.note());
        OffsetDateTime odt = request.date();
        if (odt != null)
            t.setDate(odt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
        return t;
    }
}

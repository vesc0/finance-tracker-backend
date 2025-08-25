package com.vesco.financetracker.repository;

import com.vesco.financetracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId); // All transactions for a user

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.status = :status")
    Double sumAmountByUserIdAndTypeAndStatus(@Param("userId") Long userId, @Param("type") String type,
            @Param("status") String status);

    List<Transaction> findByUserIdAndDateAfterAndStatusAndTypeIn(Long userId, LocalDateTime date, String status,
            List<String> types);

    List<Transaction> findByUserIdAndTypeAndStatus(Long userId, String type, String status);
}

package com.vesco.financetracker.repository;

import com.vesco.financetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // For login/auth

    boolean existsByEmail(String email); // For registration checks
}

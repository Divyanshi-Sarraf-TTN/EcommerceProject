package com.example.EcommerceProject.EcommerceProject.Token;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

    @Modifying
    @Transactional
    void deleteByEmail(String email);

}
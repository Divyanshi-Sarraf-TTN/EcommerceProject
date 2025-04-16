package com.example.EcommerceProject.EcommerceProject.Token;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;
import java.util.UUID;


public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

    @Modifying
    @Transactional
    void deleteByEmail(String email);

    @Query("SELECT t FROM Token t WHERE t.email = :email AND t.pair = :pair AND t.tokenType = 'REFRESH' AND t.isDeleted = false")
    Optional<Token> findValidRefreshTokenByEmailAndPair(String email, UUID pair);
    @Query("Select t FROM Token t WHERE t.pair= :pair AND t.tokenType='ACCESS' AND t.isDeleted = false")
    Optional<Token> findAccessTokenByPair(UUID pair);

    @Query("SELECT COUNT(t) > 0 FROM Token t WHERE t.token = :token and t.isDeleted = false")
    boolean existsByToken(String token);
}
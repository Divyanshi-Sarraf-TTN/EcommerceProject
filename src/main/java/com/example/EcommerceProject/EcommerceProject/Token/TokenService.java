package com.example.EcommerceProject.EcommerceProject.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public void saveToken(String email, String token, LocalDateTime expiresAt) {
        tokenRepository.save(new Token(email, token, expiresAt));
    }

    public boolean isValidToken(String token) {
        return tokenRepository.findByToken(token)
                .map(Token::isValid)
                .orElse(false);
    }

    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void cleanExpiredTokens() {
        tokenRepository.deleteExpiredTokens();
    }

    public void deleteToken(String email) {
        tokenRepository.deleteByEmail(email);
    }

}
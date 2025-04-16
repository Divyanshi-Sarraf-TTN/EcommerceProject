package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.LoginRequestDTO;
import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import com.example.EcommerceProject.EcommerceProject.JWT.JWTService;
import com.example.EcommerceProject.EcommerceProject.JWT.JwtAuthenticationManager;
import com.example.EcommerceProject.EcommerceProject.JWT.SecurityUserService;
import com.example.EcommerceProject.EcommerceProject.Repository.UserRepository;
import com.example.EcommerceProject.EcommerceProject.Token.Token;
import com.example.EcommerceProject.EcommerceProject.Token.TokenRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SecurityUserService userService;

    @Autowired
    private JwtAuthenticationManager jwtAuthenticationManager;

    public String apiLogin(LoginRequestDTO loginRequestDTO) throws MessagingException {
        logger.info("Login attempt for user: {}", loginRequestDTO.getEmail());

        User user = (User) userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> {
                    logger.error("Login failed: email {} not registered", loginRequestDTO.getEmail());
                    return new IllegalArgumentException("Email is not registered");
                });

        Authentication authentication = jwtAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("Authentication successful for email: {}", loginRequestDTO.getEmail());

        UUID pair = UUID.randomUUID();

        String accessToken = jwtService.generateToken(user, loginRequestDTO.getEmail(), new Date(System.currentTimeMillis() + 1000 * 60 * 15));
        Token token1 = new Token();
        token1.setToken(accessToken);
        token1.setTokenType("ACCESS");
        token1.setEmail(user.getEmail());
        token1.setPair(pair);
        token1.setExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15));
        tokenRepository.save(token1);
        logger.debug("Access token generated and saved for user: {}", loginRequestDTO.getEmail());

        String refreshToken = jwtService.generateToken(user, loginRequestDTO.getEmail(), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        Token token2 = new Token();
        token2.setToken(refreshToken);
        token2.setTokenType("REFRESH");
        token2.setPair(pair);
        token2.setEmail(user.getEmail());
        token2.setExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        tokenRepository.save(token2);
        logger.debug("Refresh token generated and saved for user: {}", loginRequestDTO.getEmail());

        logger.info("Login successful for user: {}", loginRequestDTO.getEmail());
        return "Logged in, Access Token: " + accessToken + "\nRefresh Token: " + refreshToken;
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        logger.info("Attempt to generate new Access Token from Refresh Token");

        Token storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    logger.error("Invalid Refresh Token: Token not found in DB");
                    return new RuntimeException("Invalid Refresh Token");
                });

        if (!storedToken.isValid()) {
            logger.warn("Refresh Token is invalid or expired");
            throw new RuntimeException("Refresh Token expired or revoked");
        }

        String username = jwtService.extractUserName(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (!jwtService.validateToken(refreshToken, userDetails)) {
            logger.error("Invalid Refresh Token signature or expiration for user: {}", username);
            throw new RuntimeException("Invalid Refresh Token Signature or Expired");
        }

        logger.info("Refresh token validated for user: {}", username);

        Token oldAccessToken = tokenRepository.findAccessTokenByPair(storedToken.getPair())
                .orElseThrow(() -> {
                    logger.error("Old Access Token not found for token pair: {}", storedToken.getPair());
                    return new RuntimeException("Invalid Token");
                });

        oldAccessToken.setIsDeleted(true);
        tokenRepository.save(oldAccessToken);
        logger.info("Old Access Token marked as deleted");

        User user = (User) userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found by email: {}", username);
                    return new RuntimeException("User does not exist");
                });

        Date accessExpiry = new Date(System.currentTimeMillis() + 15 * 60 * 1000);
        String newAccessToken = jwtService.generateToken(user, username, accessExpiry);

        Token accessTokenEntity = new Token();
        accessTokenEntity.setToken(newAccessToken);
        accessTokenEntity.setTokenType("ACCESS");
        accessTokenEntity.setEmail(user.getEmail());
        accessTokenEntity.setExpiresAt(accessExpiry);
        accessTokenEntity.setPair(storedToken.getPair());
        tokenRepository.save(accessTokenEntity);

        logger.info("New Access Token issued for user: {}", username);
        return newAccessToken;
    }

    public String logout(String accessToken) {
        logger.info("Logout request received for token: {}", accessToken);

        Token token = tokenRepository.findByToken(accessToken)
                .orElseThrow(() -> {
                    logger.error("Access Token not found: {}", accessToken);
                    return new IllegalArgumentException("Invalid Token");
                });

        if (!token.isValid()) {
            logger.warn("Access Token has already expired or been revoked");
            return "Token has expired";
        }

        String email = token.getEmail();

        Token refreshToken = tokenRepository.findValidRefreshTokenByEmailAndPair(email, token.getPair())
                .orElseThrow(() -> {
                    logger.error("Refresh Token not found for user: {}", email);
                    return new RuntimeException("Refresh Token not found");
                });

        token.setIsDeleted(true);
        refreshToken.setIsDeleted(true);

        tokenRepository.save(token);
        tokenRepository.save(refreshToken);

        logger.info("User {} logged out successfully", email);
        return "Successfully Logged out";
    }
}




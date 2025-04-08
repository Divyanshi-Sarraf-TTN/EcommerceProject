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

        Authentication authentication = jwtAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email is not registered"));

        UUID pair = UUID.randomUUID();

        String accessToken = jwtService.generateToken(user, loginRequestDTO.getEmail(), new Date(System.currentTimeMillis() + 1000 * 60 * 15));
        Token token1 = new Token();
        token1.setToken(accessToken);
        token1.setTokenType("ACCESS");
        token1.setEmail(user.getEmail());
        token1.setPair(pair);
        token1.setExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15));
        tokenRepository.save(token1);

        String refreshToken = jwtService.generateToken(user , loginRequestDTO.getEmail() , new Date(System.currentTimeMillis() + 1000 * 60 * 60 *24));
        Token token2 = new Token();
        token2.setToken(refreshToken);
        token2.setTokenType("REFRESH");
        token2.setPair(pair);
        token2.setEmail(user.getEmail());
        token2.setExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        tokenRepository.save(token2);

        return "Logged in , access token : " + accessToken + "\n Refresh Token:"+ refreshToken;
    }


    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        // 1. Validate the token exists in DB
        Token storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

        if (!storedToken.isValid()){
            throw new RuntimeException("Refresh Token expired or revoked");}

        // 2. Validate token structure and extract username
        String username = jwtService.extractUserName(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (!jwtService.validateToken(refreshToken, userDetails)) {
            throw new RuntimeException("Invalid Refresh Token Signature or Expired");
        }
        //delete old access token
        Token oldAccessToken=tokenRepository.findAccessTokenByPair(storedToken.getPair()).orElseThrow(()->new RuntimeException("Invalid Token"));
        oldAccessToken.setIsDeleted(true);
// 3. Generate new Access Token
        User user = (User) userRepository.findByEmail(username).orElseThrow(()->new RuntimeException("user doesnot exist"));
        Date accessExpiry = new Date(System.currentTimeMillis() + 15 * 60 * 1000); // 15 min
        String newAccessToken = jwtService.generateToken(user, username, accessExpiry);

        // 4. Store the new Access Token
        Token accessTokenEntity = new Token();
        accessTokenEntity.setToken(newAccessToken);
        accessTokenEntity.setTokenType("ACCESS");
        accessTokenEntity.setEmail(user.getEmail());
        accessTokenEntity.setExpiresAt(accessExpiry);

        tokenRepository.save(accessTokenEntity);

        return newAccessToken;
    }

    public String logout(String accessToken){
        Token token =  tokenRepository.findByToken(accessToken)
                .orElseThrow(()-> new IllegalArgumentException("Invalid Token"));

        if(!token.isValid()){
            return "Token has expired";
        }

        String email= token.getEmail();
        //tokenRepository.delete(token);
        Token refreshToken = tokenRepository.findValidRefreshTokenByEmailAndPair(email,token.getPair())
                .orElseThrow(()-> new RuntimeException("Refresh Token not found"));

        refreshToken.setIsDeleted(true);
        token.setIsDeleted(true);
        tokenRepository.save(token);
        tokenRepository.save(refreshToken);

        return "Successfully Logged out";
    }
}

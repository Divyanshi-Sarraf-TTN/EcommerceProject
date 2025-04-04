package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.ForgotPasswordDTO;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import com.example.EcommerceProject.EcommerceProject.Repository.UserRepository;
import com.example.EcommerceProject.EcommerceProject.Token.Token;
import com.example.EcommerceProject.EcommerceProject.Token.TokenRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TokenRepository tokenRepository;



    public String forgotPassword(ForgotPasswordDTO) throws MessagingException {
        //email should exist in db
        User user = (User) userRepository.findByEmail(F)
                .orElseThrow(() -> new IllegalArgumentException("Email should exist in db"));
        //user must be active
        if (!user.isActive()) {
            throw new RuntimeException("Customer already active");
        }
        //delete the token
        tokenRepository.deleteByEmail(email);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);
        Token tokenEntity = new Token(user.getEmail(), token, expiryTime);
        tokenRepository.save(tokenEntity);
        sendActivationEmail(email, token);
        return "New forgotpassword email sent!";

    }
    @Async
    public void sendActivationEmail(String email, String token) throws MessagingException {
        String activationLink = "http://localhost:8080/customers/forotpassword?token=" + token;
        String emailBody = "Click the link to reset password to your account: " + activationLink;
        emailService.sendEmail(email, "Reset Password to Your Account", emailBody);
    }
    public String updatePassword(String password,String confirmPassword,String token)
    {
        //token is expired
        Token tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid  token"));
        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {



            tokenRepository.delete(tokenEntity);
            return " token is expired. Password cannot be updated";


        }




    }
}

package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.ForgotPasswordDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.ResetPasswordDTO;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import com.example.EcommerceProject.EcommerceProject.Repository.UserRepository;
import com.example.EcommerceProject.EcommerceProject.Token.Token;
import com.example.EcommerceProject.EcommerceProject.Token.TokenRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    PasswordEncoder passwordEncoder;



    public String forgotPassword(ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        //email should exist in db
        User user = (User) userRepository.findByEmail(forgotPasswordDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email should exist in db"));
        //user must be active
        if (!user.isActive()) {
            throw new RuntimeException("user is not active");
        }
        //delete the token
        tokenRepository.deleteByEmail(forgotPasswordDTO.getEmail());
        //generate token
        String token = UUID.randomUUID().toString();
        Date expiryTime = new Date(System.currentTimeMillis() + 1 * 60 * 1000);
        //save token
        Token tokenEntity = new Token(user.getEmail(), token, expiryTime);
        tokenRepository.save(tokenEntity);
        //send mail
        sendActivationEmail(forgotPasswordDTO.getEmail(), token);
        return "Mail to reset password is send successfully!";

    }
    @Async
    public void sendActivationEmail(String email, String token) throws MessagingException {
        String activationLink = "http://localhost:8080/customers/forgotpassword?token=" + token;
        String emailBody = "Click the link to reset password to your account: " + activationLink;
        emailService.sendEmail(email, "Reset Password to Your Account", emailBody);
    }
    public String updatePassword(ResetPasswordDTO resetPasswordDTO)
    {
        if (!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        //token is expired

            Token tokenEntity = tokenRepository.findByToken(resetPasswordDTO.getToken())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid activation token"));
        if (new Date().after(tokenEntity.getExpiresAt())) {



            tokenRepository.deleteByEmail(tokenEntity.getEmail());
            return " token is expired. Password cannot be updated";


        }


            User user = (User) userRepository.findByEmail(tokenEntity.getEmail())
                    .orElseThrow(()-> new IllegalArgumentException("Invalid data"));

            user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
            userRepository.save(user);
        tokenRepository.deleteByEmail(tokenEntity.getEmail());
            return "Password is updated";


    }
}
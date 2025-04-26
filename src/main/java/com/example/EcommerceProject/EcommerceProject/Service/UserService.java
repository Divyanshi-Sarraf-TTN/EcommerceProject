package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.AddressRequestDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.ForgotPasswordDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.ResetPasswordDTO;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Address;
import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import com.example.EcommerceProject.EcommerceProject.Exception.ForbiddenAccessException;
import com.example.EcommerceProject.EcommerceProject.Exception.MethodNotAllowedException;
import com.example.EcommerceProject.EcommerceProject.Exception.ResourceNotFoundException;
import com.example.EcommerceProject.EcommerceProject.Exception.UnauthorizedAccessException;
import com.example.EcommerceProject.EcommerceProject.Repository.AddressRepository;
import com.example.EcommerceProject.EcommerceProject.Repository.UserRepository;
import com.example.EcommerceProject.EcommerceProject.Token.Token;
import com.example.EcommerceProject.EcommerceProject.Token.TokenRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AddressRepository addressRepository;


    public String forgotPassword(ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        logger.info("Attempting forgot password process for email: {}", forgotPasswordDTO.getEmail());

        User user = (User) userRepository.findByEmail(forgotPasswordDTO.getEmail())
                .orElseThrow(() -> {
                    logger.error("Email not found in DB: {}", forgotPasswordDTO.getEmail());
                    return new ResourceNotFoundException("Email should exist in db");
                });

        if (!user.isActive()) {
            logger.warn("Inactive user attempted to reset password: {}", forgotPasswordDTO.getEmail());
            throw new ForbiddenAccessException("User is not active");
        }

        tokenRepository.deleteByEmail(forgotPasswordDTO.getEmail());
        String token = UUID.randomUUID().toString();
        Date expiryTime = new Date(System.currentTimeMillis() + 15 * 60 * 1000);
        Token tokenEntity = new Token(user.getEmail(), token, expiryTime);
        tokenRepository.save(tokenEntity);

        sendActivationEmail(forgotPasswordDTO.getEmail(), token);
        logger.info("Reset password token generated and email sent for: {}", forgotPasswordDTO.getEmail());

        return "Mail to reset password is sent successfully!";
    }

    @Async
    public void sendActivationEmail(String email, String token) throws MessagingException {
        String activationLink = "http://localhost:8080/customers/forgotpassword?token=" + token;
        String emailBody = "Click the link to reset password to your account: " + activationLink;
        emailService.sendEmail(email, "Reset Password to Your Account", emailBody);
        logger.info("Reset password email sent to {}", email);
    }

    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
        logger.info("Attempting to update password using reset token");

        if (!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            logger.error("Passwords do not match");
            throw new MethodNotAllowedException("Passwords do not match");
        }

        Token tokenEntity = tokenRepository.findByToken(resetPasswordDTO.getToken())
                .orElseThrow(() -> {
                    logger.error("Invalid token used for password reset: {}", resetPasswordDTO.getToken());
                    return new UnauthorizedAccessException("Invalid activation token");
                });

        if (new Date().after(tokenEntity.getExpiresAt())) {
            logger.warn("Token expired for email: {}", tokenEntity.getEmail());
            tokenRepository.deleteByEmail(tokenEntity.getEmail());
            return "Token is expired. Password cannot be updated";
        }

        User user = (User) userRepository.findByEmail(tokenEntity.getEmail())
                .orElseThrow(() -> {
                    logger.error("No user found for token email: {}", tokenEntity.getEmail());
                    return new ResourceNotFoundException("Invalid data");
                });

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
        userRepository.save(user);
        tokenRepository.deleteByEmail(tokenEntity.getEmail());

        logger.info("Password updated successfully for {}", tokenEntity.getEmail());
        return "Password is updated";
    }

    public String updatePasswordbysellerandcustomer(String token, ResetPasswordDTO resetPasswordDTO) {
        logger.info("Updating password using token for seller/customer");

        if (!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            logger.error("Passwords do not match");
            throw new MethodNotAllowedException("password do not match");
            //return  "Password do not match";
        }

        Token token1 = tokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.error("Invalid token provided: {}", token);
                    return new RuntimeException("No token found");
                });

        User user1 = (User) userRepository.findByEmail(token1.getEmail())
                .orElseThrow(() -> {
                    logger.error("No user found with email: {}", token1.getEmail());
                    return new ResourceNotFoundException("No user found in database with email");
                });

        user1.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
        userRepository.save(user1);

        logger.info("Password updated successfully for {}", token1.getEmail());
        return "Password updated successfully";
    }

    public String addressUpdatebySellerandCustomer(String token, Long id, AddressRequestDTO addressRequestDTO) {
        logger.info("Updating address ID {} for token {}", id, token);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Address ID not found: {}", id);
                    return new ResourceNotFoundException("Id does not exist");
                });

        if (addressRequestDTO.getCity() != null && !addressRequestDTO.getCity().isBlank()) {
            address.setCity(addressRequestDTO.getCity());
        }
        if (addressRequestDTO.getState() != null && !addressRequestDTO.getState().isBlank()) {
            address.setState(addressRequestDTO.getState());
        }
        if (addressRequestDTO.getCountry() != null && !addressRequestDTO.getCountry().isBlank()) {
            address.setCountry(addressRequestDTO.getCountry());
        }
        if (addressRequestDTO.getZipCode() != null) {
            address.setZipCode(addressRequestDTO.getZipCode());
        }
        if (addressRequestDTO.getAddressLine() != null && !addressRequestDTO.getAddressLine().isBlank()) {
            address.setAddressLine(addressRequestDTO.getAddressLine());
        }
        if (addressRequestDTO.getLabel() != null && !addressRequestDTO.getLabel().isBlank()) {
            address.setLabel(addressRequestDTO.getLabel());
        }

        addressRepository.save(address);
        logger.info("Address updated successfully for ID {}", id);
        return "Address updated successfully";
    }
}

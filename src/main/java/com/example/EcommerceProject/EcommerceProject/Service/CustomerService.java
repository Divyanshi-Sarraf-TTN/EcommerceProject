package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.CustomerRequestDTO;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import com.example.EcommerceProject.EcommerceProject.Repository.CustomerRepository;
import com.example.EcommerceProject.EcommerceProject.Token.Token;
import com.example.EcommerceProject.EcommerceProject.Token.TokenRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Transactional
    public String registerCustomer(CustomerRequestDTO request) throws MessagingException {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email id already registered");
        }

        if (customerRepository.findByContact(request.getContact()).isPresent()) {
            throw new IllegalArgumentException("Contact Number is duplicate");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setContact(request.getContact());
        customer.setPassword(request.getPassword()); // Hash this in production
        customer.setActive(false);
        customer.setPasswordUpdateDate(LocalDate.from(LocalDateTime.now()));
        customerRepository.save(customer);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);

        Token tokenEntity = new Token(customer.getEmail(), token, expiryTime);
        tokenRepository.save(tokenEntity);

        sendActivationEmail(customer.getEmail(), token);

        return "Customer registered successfully! Please check your email to activate your account.";
    }

    @Async
    public void sendActivationEmail(String email, String token) throws MessagingException {
        String activationLink = "http://localhost:8080/customers/activate?token=" + token;
        String emailBody = "Click the link to activate your account: " + activationLink;
        emailService.sendEmail(email, "Activate Your Account", emailBody);
    }

    public String activateCustomer(String token) throws MessagingException {
        Token tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid activation token"));

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {

            String newToken = UUID.randomUUID().toString();
            LocalDateTime newExpiryTime = LocalDateTime.now().plusMinutes(1);

            Token newTokenEntity = new Token(tokenEntity.getEmail(), newToken, newExpiryTime);
            tokenRepository.save(newTokenEntity);

            sendActivationEmail(tokenEntity.getEmail(), newToken);

            tokenRepository.delete(tokenEntity);

            return "Activation token has expired. Please check your email for a new token.";
        }

        Customer customer = (Customer) customerRepository.findByEmail(tokenEntity.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No account found for token"));

        customer.setActive(true);
        customerRepository.save(customer);
        tokenRepository.delete(tokenEntity);

        return "Account activated successfully!";
    }

    public String resendactivate(String email) throws MessagingException {
        Customer customer = (Customer) customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not registered"));

        if (customer.isActive()) {
            throw new RuntimeException("Customer already active");
        }

        tokenRepository.deleteByEmail(email);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);

        Token tokenEntity = new Token(customer.getEmail(), token, expiryTime);
        tokenRepository.save(tokenEntity);

        sendActivationEmail(email, token);
        return "New activation email sent!";
    }
}

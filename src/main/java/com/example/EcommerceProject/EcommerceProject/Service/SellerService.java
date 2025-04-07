package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.SellerRequestDTO;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Role;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import com.example.EcommerceProject.EcommerceProject.Repository.RoleRepository;
import com.example.EcommerceProject.EcommerceProject.Repository.SellerRepository;
import com.example.EcommerceProject.EcommerceProject.Token.Token;
import com.example.EcommerceProject.EcommerceProject.Token.TokenRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class SellerService {
@Autowired
    private SellerRepository sellerRepository;
@Autowired
    private EmailService emailService;
@Autowired
    private TokenRepository tokenRepository;
@Autowired
private RoleRepository roleRepository;
public String registerSeller(SellerRequestDTO request)throws MessagingException{


    if (sellerRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new IllegalArgumentException("Email id already registered");
    }

    if (sellerRepository.findByGst(request.getGst()).isPresent()) {
        throw new IllegalArgumentException("Gst should be unique");

    }
    if (sellerRepository.findByCompanyName(request.getCompanyName()).isPresent()) {
        throw new IllegalArgumentException("CompanyName should be unique");

    }

    Seller seller = new Seller();
    seller.setFirstName(request.getFirstName());
    seller.setLastName(request.getLastName());
    seller.setEmail(request.getEmail());
    seller.setCompanyContact(request.getCompanyContact());
    seller.setCompanyName(request.getCompanyName());
    seller.setAddress(request.getCompanyAddress());
    seller.setGst(request.getGst());
    seller.setLocked(false);
    Role role = roleRepository.findByAuthority("SELLER")
            .orElseThrow(()-> new RuntimeException("Not Found"));

    seller.setRole(role);
    seller.setPassword(request.getPassword());


    // Hash this in production
    seller.setActive(false);
    seller.setPasswordUpdateDate(LocalDate.from(LocalDateTime.now()));
    sellerRepository.save(seller);
    String token = UUID.randomUUID().toString();
    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);

    Token tokenEntity = new Token(seller.getEmail(), token, expiryTime);
    tokenRepository.save(tokenEntity);

    sendActivationEmail(seller.getEmail());

    return "Seller registered successfully! Please check your email .";

}
    @Async
    public void sendActivationEmail(String email) throws MessagingException {

        String emailBody = "Account has been created !Waiting for approval " ;
        emailService.sendEmail(email, "Account created", emailBody);
    }

}

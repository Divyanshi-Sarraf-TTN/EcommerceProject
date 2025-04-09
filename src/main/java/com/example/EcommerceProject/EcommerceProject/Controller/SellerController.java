package com.example.EcommerceProject.EcommerceProject.Controller;


import com.example.EcommerceProject.EcommerceProject.DTO.LoginRequestDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.SellerRequestDTO;
import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
import com.example.EcommerceProject.EcommerceProject.Service.SellerService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/sellers")
public class SellerController {

    @Autowired
    private SellerService sellerService;
    @Autowired
    private LoginService loginService;

    @PostMapping("/register")
    ResponseEntity<String> register(@RequestBody @Valid SellerRequestDTO request) throws MessagingException {
        return ResponseEntity.ok(sellerService.registerSeller(request));
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO)throws MessagingException {
        return ResponseEntity.ok(loginService.apiLogin(loginRequestDTO));
    }
    @PostMapping("/generateAccessToken")
    public ResponseEntity<String> generateToken(@RequestParam String refreshToken)throws  MessagingException{
        return ResponseEntity.ok(loginService.generateAccessTokenFromRefreshToken(refreshToken));
    }
}
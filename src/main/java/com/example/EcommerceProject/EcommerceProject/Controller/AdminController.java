package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.DTO.LoginRequestDTO;
import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminController {
    @Autowired
    private LoginService loginService;
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO)throws MessagingException {
        return ResponseEntity.ok(loginService.apiLogin(loginRequestDTO));
    }
    @PostMapping("/generateAccessToken")
    public ResponseEntity<String> generateToken(@RequestParam String refreshToken)throws  MessagingException{
        return ResponseEntity.ok(loginService.generateAccessTokenFromRefreshToken(refreshToken));
    }
}

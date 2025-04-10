package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.DTO.CustomerResponseDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.LoginRequestDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.SellerResponseDTO;
//import com.example.EcommerceProject.EcommerceProject.Service.AdminService;
import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO)throws MessagingException {
        return ResponseEntity.ok(loginService.apiLogin(loginRequestDTO));
    }
    @PostMapping("/generateAccessToken")
    public ResponseEntity<String> generateToken(@RequestParam String refreshToken)throws  MessagingException{
        return ResponseEntity.ok(loginService.generateAccessTokenFromRefreshToken(refreshToken));
    }

}

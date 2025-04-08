package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LogoutController {
    @Autowired
    private LoginService loginService;
    @PostMapping("/customer/logout")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<String>customerLogout(@RequestParam String accessToken){
        return ResponseEntity.ok(loginService.logout(accessToken));}
    @PostMapping("/seller/logout")


    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<String> sellerLogout(@RequestParam String accessToken){
        return ResponseEntity.ok(loginService.logout(accessToken));
    }

    @PostMapping("/admin/logout")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<String> adminLogout(@RequestParam String accessToken){
        return ResponseEntity.ok(loginService.logout(accessToken));
    }

    }


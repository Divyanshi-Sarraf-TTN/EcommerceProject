package com.example.EcommerceProject.EcommerceProject.Controller;


import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

//isme bearer token  me access token daalna padega
    @GetMapping("/customer/dashboard")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<String> customerDashboard() {
        return ResponseEntity.ok("Welcome, Customer!");
    }
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(@RequestParam String accessToken){
//        return ResponseEntity.ok(loginService.logout(accessToken));
//    }
    @GetMapping("/seller/dashboard")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<String> sellerDashboard(){
        return ResponseEntity.ok("Welcome,Seller");
    }
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Welcome, Admin!");
    }


}
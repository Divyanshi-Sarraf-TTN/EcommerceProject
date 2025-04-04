package com.example.EcommerceProject.EcommerceProject.Controller;


import com.example.EcommerceProject.EcommerceProject.DTO.SellerRequestDTO;
import com.example.EcommerceProject.EcommerceProject.Service.SellerService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sellers")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @PostMapping("/register")
    ResponseEntity<String> register(@RequestBody SellerRequestDTO request) throws MessagingException {
        return ResponseEntity.ok(sellerService.registerSeller(request));
    }
}
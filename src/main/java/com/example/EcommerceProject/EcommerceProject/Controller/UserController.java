package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.Service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/forgotpassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        return ResponseEntity.ok(userService.forgotPassword(email));
    }
    @PostMapping("/updatepassword")
    public ResponseEntity<String> updatePassword(@RequestParam String password,String updatePassword,String token) throws MessagingException {
        return ResponseEntity.ok(userService.updatePassword(password,updatePassword,token));
    }
}

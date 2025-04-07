package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.DTO.ForgotPasswordDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.ResetPasswordDTO;
import com.example.EcommerceProject.EcommerceProject.Service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/forgotpassword")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        System.out.println("controller");
        return ResponseEntity.ok(userService.forgotPassword(forgotPasswordDTO));
    }
    @PutMapping("/updatepassword")
    public ResponseEntity<String> updatePassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws MessagingException {
        return ResponseEntity.ok(userService.updatePassword(resetPasswordDTO));
    }
}

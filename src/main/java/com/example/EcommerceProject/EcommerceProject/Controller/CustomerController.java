package com.example.EcommerceProject.EcommerceProject.Controller;


import com.example.EcommerceProject.EcommerceProject.DTO.CustomerRequestDTO;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import com.example.EcommerceProject.EcommerceProject.Service.CustomerService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    ResponseEntity<String> register(@RequestBody CustomerRequestDTO request) throws MessagingException {
        return ResponseEntity.ok(customerService.registerCustomer(request));
    }

    @PutMapping ("/activate")
    public ResponseEntity<String> activate(@RequestParam String token) throws MessagingException {
        return ResponseEntity.ok(customerService.activateCustomer(token));
    }

    @GetMapping("/test")
    public String test() {

        return "Controller is working!";
    }
    @PostMapping("/re-sendactivation")
    public ResponseEntity<String> resendactivate(@RequestParam String email)throws MessagingException{
        return ResponseEntity.ok(customerService.resendactivate(email));
    }
}
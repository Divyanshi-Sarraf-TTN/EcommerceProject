package com.example.EcommerceProject.EcommerceProject.Controller;


import com.example.EcommerceProject.EcommerceProject.DTO.CustomerRequestDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.LoginRequestDTO;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import com.example.EcommerceProject.EcommerceProject.Service.CustomerService;
import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/customers")
public class CustomerController {


    @Autowired
    private CustomerService customerService;
  @Autowired
  private LoginService loginService;

    @PostMapping("/register")
    ResponseEntity<String> register(@RequestBody @Valid CustomerRequestDTO request) throws MessagingException {
        return ResponseEntity.ok(customerService.registerCustomer(request));
    }

    @PutMapping ("/activate")
    public ResponseEntity<String> activate(@RequestParam String token) throws MessagingException {
        return ResponseEntity.ok(customerService.activateCustomer(token));
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO)throws MessagingException{
        return ResponseEntity.ok(loginService.apiLogin(loginRequestDTO));
    }


    @GetMapping("/test")
    public String test() {
        System.out.println("In test");
        return "Controller is working!";
    }
    @PostMapping("/re-sendactivation")
    public ResponseEntity<String> resendactivate(@RequestParam String email)throws MessagingException{
        return ResponseEntity.ok(customerService.resendactivate(email));
    }
    @PostMapping("/generateAccessToken")
    public ResponseEntity<String> generateToken(@RequestParam String refreshToken)throws  MessagingException{
        return ResponseEntity.ok(loginService.generateAccessTokenFromRefreshToken(refreshToken));
    }
}
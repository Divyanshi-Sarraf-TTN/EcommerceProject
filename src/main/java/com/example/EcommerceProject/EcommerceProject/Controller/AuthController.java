package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.DTO.*;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Address;
import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
import com.example.EcommerceProject.EcommerceProject.Service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) throws MessagingException {
        return ResponseEntity.ok(loginService.apiLogin(loginRequestDTO));
    }

    @PostMapping("/auth/generateAccessToken")
    public ResponseEntity<String> generateToken(@RequestParam String refreshToken) throws MessagingException {
        return ResponseEntity.ok(loginService.generateAccessTokenFromRefreshToken(refreshToken));
    }

    @PostMapping("/auth/forgotpassword")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        System.out.println("controller");
        return ResponseEntity.ok(userService.forgotPassword(forgotPasswordDTO));
    }

    @PutMapping("/auth/updatepassword")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) throws MessagingException {
        return ResponseEntity.ok(userService.updatePassword(resetPasswordDTO));
    }


    @PostMapping("/logout")
    public ResponseEntity<String> customerLogout(HttpServletRequest request) throws BadRequestException {
        String accessToken = request.getHeader("Authorization");
        if (accessToken != null && !accessToken.isBlank() && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(loginService.logout(accessToken));
    }

    @PutMapping("/updatepassword")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_SELLER')")
    public ResponseEntity<String> updatePasswordByCustomerOrSeller(HttpServletRequest request, @RequestBody ResetPasswordDTO resetPasswordDTO) throws BadRequestException {
        String token = request.getHeader("Authorization");
        System.out.println(request);
        if (token != null && !token.isBlank() && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(userService.updatePasswordbysellerandcustomer(token, resetPasswordDTO));


    }
    @PutMapping("/updateaddress")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')or hasAuthority('ROLE_SELLER')")
    public ResponseEntity<String> updateAddressByCustomerOrSeller(HttpServletRequest request,@RequestParam long id, @RequestBody AddressRequestDTO addressRequestDTO)throws BadRequestException{
        String token = request.getHeader("Authorization");
        System.out.println(request);
        if (token != null && !token.isBlank() && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(userService.addressUpdatebySellerandCustomer(token,id,addressRequestDTO));
    }


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

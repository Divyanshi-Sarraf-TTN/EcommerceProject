package com.example.EcommerceProject.EcommerceProject.Controller;


import com.example.EcommerceProject.EcommerceProject.DTO.*;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import com.example.EcommerceProject.EcommerceProject.Service.CustomerService;
import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerController {


    @Autowired
    private CustomerService customerService;
  @Autowired
  private LoginService loginService;

    @PostMapping("/auth/customers/register")
    ResponseEntity<String> register(@RequestBody @Valid CustomerRequestDTO request) throws MessagingException {
        return ResponseEntity.ok(customerService.registerCustomer(request));
    }

    @PutMapping ("/auth/customers/activate")
    public ResponseEntity<String> activate(@RequestParam String token) throws MessagingException {
        return ResponseEntity.ok(customerService.activateCustomer(token));
    }
//    @PostMapping("/auth/customers/login")
//    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO)throws MessagingException{
//        return ResponseEntity.ok(loginService.apiLogin(loginRequestDTO));
//    }


    @GetMapping("/test")
    public String test() {
        System.out.println("In test");
        return "Controller is working!";
    }
    @PostMapping("/auth/customers/re-sendactivation")
    public ResponseEntity<String> resendactivate(@RequestParam String email)throws MessagingException{
        return ResponseEntity.ok(customerService.resendactivate(email));
    }
//    @PostMapping("/auth/customers/generateAccessToken")
//    public ResponseEntity<String> generateToken(@RequestParam String refreshToken)throws  MessagingException{
//        return ResponseEntity.ok(loginService.generateAccessTokenFromRefreshToken(refreshToken));
//    }
    @GetMapping("/viewProfileOfCustomer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerProfileResponseDTO>viewProfileOfCustomer(HttpServletRequest request) throws BadRequestException, MessagingException {
        String token = request.getHeader("Authorization");
        System.out.println(request);
        if(token!=null && !token.isBlank() && token.startsWith("Bearer ")){
            token=token.substring(7);
        }else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(customerService.viewProfileOfCustomer(token));
    }
    @GetMapping("/viewaddressesofseller")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER)")
    public ResponseEntity<List<AddressResponseDTO>> viewAddressOfCustomer(HttpServletRequest request) throws BadRequestException, MessagingException {

        String token = request.getHeader("Authorization");
        System.out.println(request);
        if(token!=null && !token.isBlank() && token.startsWith("Bearer ")){
            token=token.substring(7);
        }else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(customerService.viewAddressesOfCustomer(token));
    }
    @PutMapping("/updateProfileOfCustomer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<String> updateProfileOfCustomer(HttpServletRequest request, @RequestBody CustomerRequestDTO customerRequestDTO) throws BadRequestException, MessagingException {
        String token = request.getHeader("Authorization");
        System.out.println(request);
        if(token!=null && !token.isBlank() && token.startsWith("Bearer ")){
            token=token.substring(7);
        }else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(customerService.updateCustomerProfile(token,customerRequestDTO));
    }
    @PostMapping("/addnewAddress")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<String> addNewAddressOfCustomer(HttpServletRequest request,@RequestBody AddressRequestDTO addressRequestDTO)throws BadRequestException{
        String token = request.getHeader("Authorization");
        System.out.println(request);
        if(token!=null && !token.isBlank() && token.startsWith("Bearer ")){
            token=token.substring(7);
        }else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(customerService.addAddress(token,addressRequestDTO));
    }
    @DeleteMapping("/deleteaddress")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<String> deleteAddress(HttpServletRequest request,Long id)throws BadRequestException{
        String token = request.getHeader("Authorization");
        System.out.println(request);
        if(token!=null && !token.isBlank() && token.startsWith("Bearer ")){
            token=token.substring(7);
        }else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(customerService.deleteAddress(token,id));
    }
}
package com.example.EcommerceProject.EcommerceProject.Controller;


import com.example.EcommerceProject.EcommerceProject.DTO.CategoryMetaDataFieldValueRequest;
import com.example.EcommerceProject.EcommerceProject.DTO.SellerProfileResponseDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.SellerRequestDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.ViewLeafCategory;
import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
import com.example.EcommerceProject.EcommerceProject.Service.SellerService;
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
public class SellerController {

    @Autowired
    private SellerService sellerService;
    @Autowired
    private LoginService loginService;

    @PostMapping("/auth/sellers/register")
    ResponseEntity<String> register(@RequestBody @Valid SellerRequestDTO request) throws MessagingException {
        return ResponseEntity.ok(sellerService.registerSeller(request));
    }
//    @PostMapping("/auth/sellers/login")
//    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO)throws MessagingException {
//        return ResponseEntity.ok(loginService.apiLogin(loginRequestDTO));
//    }
//    @PostMapping("/auth/sellers/generateAccessToken")
//    public ResponseEntity<String> generateToken(@RequestParam String refreshToken)throws  MessagingException{
//        return ResponseEntity.ok(loginService.generateAccessTokenFromRefreshToken(refreshToken));
//    }
    @GetMapping("/seller/viewprofile")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<SellerProfileResponseDTO>sellerProfile(HttpServletRequest request) throws MessagingException, BadRequestException {
        String token = request.getHeader("Authorization");
        System.out.println(token);
        if(token!=null && !token.isBlank() && token.startsWith("Bearer ")){
            token=token.substring(7);
        }else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(sellerService.viewSellerProfile(token));
    }
    @PatchMapping("/seller/updateprofile")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<String> updateProfile(HttpServletRequest request,@RequestBody SellerRequestDTO sellerRequestDTO)throws MessagingException,BadRequestException{
        String token = request.getHeader("Authorization");
        System.out.println(request);
        if(token!=null && !token.isBlank() && token.startsWith("Bearer ")){
            token=token.substring(7);
        }else throw new BadRequestException("Token not found");
        return ResponseEntity.ok(sellerService.updateSellerProfile(token, sellerRequestDTO));
    }
    @GetMapping("/viewLeafCategory")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<List<ViewLeafCategory>> viewLeafCategory()
    {
        sellerService.viewLeafCategory();
        return ResponseEntity.ok(sellerService.viewLeafCategory());
    }

}
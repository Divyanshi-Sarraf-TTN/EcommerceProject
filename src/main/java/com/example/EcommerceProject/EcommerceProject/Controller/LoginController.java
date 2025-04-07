//package com.example.EcommerceProject.EcommerceProject.Controller;
//
//import com.example.EcommerceProject.EcommerceProject.DTO.AuthResponseDTO;
//import com.example.EcommerceProject.EcommerceProject.DTO.LoginRequestDTO;
//import com.example.EcommerceProject.EcommerceProject.Service.LoginService;
//import jakarta.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/users")
//public class LoginController {
//    @Autowired
//    private AuthenticationManager authenticationManager;
//    @Autowired
//    private  JwtTokenProvider jwtTokenProvider;
//    @Autowired
//private  LoginService loginService;
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponseDTO>login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
//        //Authenticate the user
//        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(),loginRequestDTO.getPassword()));
//        //generate access ad refresh token
//        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
//        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
//        return ResponseEntity.ok(new AuthResponseDTO(accessToken, refreshToken));
//    }
//
//}

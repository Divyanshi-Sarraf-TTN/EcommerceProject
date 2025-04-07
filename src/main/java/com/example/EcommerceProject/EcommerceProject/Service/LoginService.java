//package com.example.EcommerceProject.EcommerceProject.Service;
//
//import com.example.EcommerceProject.EcommerceProject.DTO.LoginRequestDTO;
//import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
//import com.example.EcommerceProject.EcommerceProject.JWT.JwtService;
//import com.example.EcommerceProject.EcommerceProject.Repository.CustomerRepository;
//import com.example.EcommerceProject.EcommerceProject.Repository.LoginRepository;
//import com.example.EcommerceProject.EcommerceProject.Token.TokenRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//public class LoginService {
//    @Autowired
//    private LoginRepository loginRepository;
//
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private TokenRepository tokenRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private JwtService jwtService;
//    private static final int MAX_ATTEMPTS = 3;
//
//    public String login(LoginRequestDTO loginRequestDTO)
//    {
//        //email should exist in db
//        User user = (User) loginRepository.findByEmail(loginRequestDTO.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("Email should exist in db"));
//        //password must exist in db
//         user=(User) loginRepository.findByPassword(loginRequestDTO.getPassword()).orElseThrow(()->new IllegalArgumentException("password must exist in db"));
//         //account must be active
//        if (!user.isActive()) {
//            throw new RuntimeException("user is not active");
//        }
//        //account must not be locked
//        if(user.isLocked()){
//            throw new RuntimeException("Account is locked");
//        }
//        //if password doesnot matches
//        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())){
//            int failedAttempts=user.getInvalidAttemptCount()+1;
//            user.setInvalidAttemptCount(failedAttempts);
//            //lock account if attempt exceed
//
//            if(failedAttempts>=MAX_ATTEMPTS)
//            {
//                user.setLocked(true);
//            }
//            loginRepository.save(user);
//            throw new RuntimeException("Invalid password");
//        }
//        //successful login,reset failed attempts
//        user.setInvalidAttemptCount(0);
//        loginRepository.save(user);
//        //generate token
//        String accessToken=jwtService.generateToken(user,loginRequestDTO.getEmail());
//        String refreshToken=jwtService.refreshToken(user,loginRequestDTO.getEmail());
//        return "logged in ,access token: "+accessToken+"/n refresh token"+refreshToken;
//    }
//}

//package com.example.EcommerceProject.EcommerceProject.JWT;
//
//import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
///**
// * UserInfoService provides user-related services including loading user details
// * and managing user data in the repository.
// */
//@Service
//public class UserService implements UserDetailsService {
//
//    @Autowired
//    private UserRepo userRepo;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//
//    // Loads a user's details given their userName.
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> user = userRepo.findByUserName(username);
//        return user.map(UserPrincipal::new)
//                .orElseThrow(() -> new UsernameNotFoundException("UserName not found: " + username));
//    }
//
//
//    // Adds a new user to the repository and encrypting password before saving it.
//    public String addUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userRepo.save(user);
//        return "user added successfully";
//    }
//
//
//}
//

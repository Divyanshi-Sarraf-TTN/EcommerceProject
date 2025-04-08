package com.example.EcommerceProject.EcommerceProject.JWT;

import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import com.example.EcommerceProject.EcommerceProject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //System.out.println("loadUser : "  + userName);
        return userRepository.findByEmail(userName)
                .map(user -> new CustomUserDetails((User) user))

                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userName));
    }
}

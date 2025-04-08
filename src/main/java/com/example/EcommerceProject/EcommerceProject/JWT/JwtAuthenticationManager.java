package com.example.EcommerceProject.EcommerceProject.JWT;




import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import com.example.EcommerceProject.EcommerceProject.Repository.UserRepository;
import com.example.EcommerceProject.EcommerceProject.Service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationManager implements AuthenticationManager {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email is not registered"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setInvalidAttemptCount(user.getInvalidAttemptCount() + 1);

            if (user.getInvalidAttemptCount() >= 3) {
                user.setLocked(true);
                userRepository.save(user);
                try {
                    emailService.sendEmail(email, "Account Locked", "Your account is locked.");
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
                throw new LockedException("Account locked due to multiple invalid attempts");
            }

            userRepository.save(user);
            throw new BadCredentialsException("Invalid password");
        }

        if (!user.isActive()) {
            throw new DisabledException("Account is not active");
        }

        if (user.isLocked()) {
            throw new LockedException("Account is locked");
        }

        if (user.isExpired()) {
            throw new CredentialsExpiredException("Password expired, update required");
        }

        // Reset invalid attempts on successful login
        user.setInvalidAttemptCount(0);
        userRepository.save(user);

        return new UsernamePasswordAuthenticationToken(user, password);
    }



}
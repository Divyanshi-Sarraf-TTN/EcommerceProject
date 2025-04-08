package com.example.EcommerceProject.EcommerceProject.JWT;

import com.example.EcommerceProject.EcommerceProject.JWT.JWTFilter;
import com.example.EcommerceProject.EcommerceProject.JWT.SecurityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration
@EnableWebSecurity
@CrossOrigin
public class SecurityConfig {

    @Autowired
    private JWTFilter jwtFilter;

    // Defines a UserDetailsService bean for user authentication
    @Bean
    public UserDetailsService userDetailsService() {
        return new SecurityUserService();
    }


    //Configures the security filter chain
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .cors(Customizer.withDefaults()) // Apply CORS
//                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/addUser", "/auth/login")
//                        .permitAll()// Permit all requests to certain URLs
//                        .anyRequest().authenticated()) // Require authentication for all other requests
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session management to stateless
//                .authenticationProvider(authenticationProvider()) // Register the authentication provider
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Add the JWT filter before processing the request
//                .build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // Disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**")
                        .permitAll()
                        .requestMatchers("/api/customer/**")
                        .hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers("/api/seller/**")
                        .hasAuthority("ROLE_SELLER")
                        .requestMatchers("/api/admin/**")
                        .hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()) // Allow all requests
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    // Creates a DaoAuthenticationProvider to handle user authentication
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    // Defines a PasswordEncoder bean that uses bcrypt hashing by default for password encoding
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Defines an AuthenticationManager bean to manage authentication processes
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
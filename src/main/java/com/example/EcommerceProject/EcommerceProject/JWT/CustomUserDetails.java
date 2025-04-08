package com.example.EcommerceProject.EcommerceProject.JWT;

import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private String email = null;
    private String password = null;
    private Set<SimpleGrantedAuthority> authorities;

    // public CustomUserDetails(Object o) {}

    public CustomUserDetails(User user) {
        System.out.println("constructor userdetial : " + user.getEmail());
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole().getAuthority()));
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        System.out.println("getName method "  + email);
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Changed from UserDetails.super call (which is invalid in interface implementation)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}


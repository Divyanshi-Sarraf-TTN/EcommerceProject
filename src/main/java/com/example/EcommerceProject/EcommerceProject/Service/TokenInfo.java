package com.example.EcommerceProject.EcommerceProject.Service;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenInfo {
    private String email;
    private LocalDateTime expiry;

    public TokenInfo(String email, LocalDateTime expiry) {
        this.email = email;
        this.expiry = expiry;
    }

}

package com.example.EcommerceProject.EcommerceProject.Token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter

public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Date expiresAt;

    private String tokenType;

    private Boolean isDeleted = false;

    private UUID pair;

    // Constructors
    public Token() {}

    public Token(String email, String token, Date expiresAt) {
        this.email=email;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public boolean isValid() {
        return new Date().before(this.expiresAt);
    }
}
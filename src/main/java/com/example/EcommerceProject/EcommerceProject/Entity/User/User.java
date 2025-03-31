package com.example.EcommerceProject.EcommerceProject.Entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)

 private Integer id;
private String email;
private String firstName;
private String lastName;
private String middleName;
private String  password;
private boolean isDeleted;
private boolean isActive;
private boolean isExpired;
private boolean isLocked;
private Integer invalidAttemptCount;
private LocalDate passwordUpdateDate;
@ManyToMany(mappedBy = "users")
    private List<Role> roles;


}

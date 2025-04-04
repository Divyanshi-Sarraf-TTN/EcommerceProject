package com.example.EcommerceProject.EcommerceProject.Entity.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
 private Long id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
private String lastName;

    @Size(max = 50, message = "Middle name cannot exceed 50 characters")
private String middleName;

  @NotBlank(message = "Password is required")
   @Size(min = 8, message = "Password must be at least 8 characters long")
private String  password;

private boolean isDeleted;
private boolean isActive;
private boolean isExpired;
private boolean isLocked;

//    @Min(value = 0, message = "Invalid attempts cannot be negative")
private Integer invalidAttemptCount;

private LocalDate passwordUpdateDate;

@ManyToOne
@JoinColumn(name="role")
    private Role role;


}

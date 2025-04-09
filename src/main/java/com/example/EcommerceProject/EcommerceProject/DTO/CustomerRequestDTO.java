package com.example.EcommerceProject.EcommerceProject.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CustomerRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Contact number cannot be null")
    @Min(value = 1000000000, message = "Contact number must be at least 10 digits")
    @Max(value = 9999999999L, message = "Contact number must be at most 10 digits")
    private Long contact;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%?&])[A-Za-z\\d@$!%?&]{8,15}$",
            message = "Password must have at least one lowercase, one uppercase, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}

package com.example.EcommerceProject.EcommerceProject.DTO;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Address;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class SellerRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%?&])[A-Za-z\\d@$!%?&]{8,15}$",
            message = "Password must have at least one lowercase, one uppercase, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @NotBlank(message = "GST is required")
    @Pattern(regexp = "^\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[A-Z\\d]{1}[Z]{1}[A-Z\\d]{1}$", message = "Invalid GST format")
    private String gst;

    @NotBlank(message = "Company Name is required")
    private String companyName;

    @NotNull(message = "Company Address is required")
    private Address companyAddress;

    @NotNull(message = "Company Contact is required")
    @Min(value = 1000000000, message = "Contact number must be at least 10 digits")
    @Max(value = 9999999999L, message = "Contact number must be at most 10 digits")
    private Long companyContact;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    // Getters and Setters
}
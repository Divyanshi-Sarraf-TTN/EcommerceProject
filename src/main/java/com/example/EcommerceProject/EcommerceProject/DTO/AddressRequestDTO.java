package com.example.EcommerceProject.EcommerceProject.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddressRequestDTO {

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    private String country;

    @NotBlank(message = "Address line is required")
    @Size(min = 5, max = 255, message = "Address line must be between 5 and 255 characters")
    private String addressLine;

    @NotNull(message = "Zip code is required")
    @Digits(integer = 6, fraction = 0, message = "Zip code must be exactly 6 digits")
    private Integer zipCode;

    @NotBlank(message = "Label is required")
    @Size(min = 3, max = 20, message = "Label must be between 3 and 20 characters (e.g., Home, Work)")
    private String label;



}

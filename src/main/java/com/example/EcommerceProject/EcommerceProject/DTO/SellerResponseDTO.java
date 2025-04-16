package com.example.EcommerceProject.EcommerceProject.DTO;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Address;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SellerResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private Boolean isActive;
    private String companyName;
    private Address Address;
    private Long companyContact;
    private String Gst;

}

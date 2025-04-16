package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private Long contact;
    private String image;
}

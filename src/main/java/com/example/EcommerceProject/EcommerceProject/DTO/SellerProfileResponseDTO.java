package com.example.EcommerceProject.EcommerceProject.DTO;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SellerProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Long companyContact;
    private String companyName;
    private String image;
    private String gst;
    private Address address;
}

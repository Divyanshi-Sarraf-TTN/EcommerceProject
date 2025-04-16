package com.example.EcommerceProject.EcommerceProject.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AddressResponseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String city;



    private String state;



    private String country;



    private String addressLine;



    private Integer zipCode;



    private String label;




}

package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ProductResponseDTO {
    private Long id;
    private String name;
    private String brand;
    private boolean isActive;
}

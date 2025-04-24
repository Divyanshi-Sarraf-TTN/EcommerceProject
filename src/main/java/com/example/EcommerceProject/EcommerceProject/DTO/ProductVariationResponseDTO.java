package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class ProductVariationResponseDTO {
    private Long id;
    private Double price;
    private Integer quantityAvailable;
    private String primaryImageUrl;
    private boolean isActive;
    private Map<String, Object> metadata;
    private ProductResponseDTO product;
}



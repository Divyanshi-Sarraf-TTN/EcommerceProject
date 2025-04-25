package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllProductResponse {
    private Long id;
    private String name;
    private String brand;
    private Long sellerId;
    private CategoryDTO category;
    private List<String> variationPrimaryImages;
}

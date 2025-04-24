package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private Boolean isCancellable;
    private Boolean isReturnable;
    private Boolean isActive;
    private CategoryResponse category;
    private List<ProductVariationResponse> productVariations;
}

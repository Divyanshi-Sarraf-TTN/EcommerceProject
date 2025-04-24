package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.*;

import java.util.Map;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationResponse {
    private Long id;
    private Double price;
    private Integer quantityAvailable;
    private Map<String, Object> metadata;
    private String primaryImageName;
}

package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {
    private Long id;
    private String name;
    private String brand;
    private Double minPrice;
    private String categoryName;
    private String primaryImage;
}

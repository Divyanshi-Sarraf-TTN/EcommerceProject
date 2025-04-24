package com.example.EcommerceProject.EcommerceProject.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ProductVariationDTO {
    @NotNull
    private Long productId;


    private Integer quantityAvailable;


    private Double price;

    private MultipartFile primaryImage;
    private List<MultipartFile> secondaryImages;

    @NotBlank(message = "Metadata is required")
    private String metadata;
}

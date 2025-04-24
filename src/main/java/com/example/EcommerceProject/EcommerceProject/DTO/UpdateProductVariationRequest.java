package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductVariationRequest {
    private Integer quantityAvailable;
    private Double price;
    private MultipartFile primaryImageName;
    private Boolean isActive;
    private String metadata;
    private List secondaryImages;
}

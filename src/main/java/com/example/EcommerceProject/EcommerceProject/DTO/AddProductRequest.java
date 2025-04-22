package com.example.EcommerceProject.EcommerceProject.DTO;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;


    private String description;

    @NotBlank(message = "Brand is required")
    @Size(min = 2, max = 100, message = "Brand must be between 2 and 100 characters")
    private String brand;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // Nullable booleans: Use wrapper class `Boolean`, not primitive `boolean`
    private Boolean isCancellable=false;

    private Boolean isReturnable=false;
}

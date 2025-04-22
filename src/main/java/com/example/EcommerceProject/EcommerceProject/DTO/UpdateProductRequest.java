package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.Getter;

@Getter
public class UpdateProductRequest {
    private String name;
    private String description;
    private Boolean isCancellable;
    private Boolean isReturnable;
}

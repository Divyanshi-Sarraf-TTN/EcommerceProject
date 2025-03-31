package com.example.EcommerceProject.EcommerceProject.Entity.Product;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
public class ProductReviewID implements Serializable {
 @NotNull(message = "Customer User ID is required")
 @Positive(message = "Customer User ID must be a positive number")
 private Long customerUserId;

 @NotNull(message = "Product ID is required")
 @Positive(message = "Product ID must be a positive number")
 private Long productId;



}

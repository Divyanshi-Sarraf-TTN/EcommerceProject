package com.example.EcommerceProject.EcommerceProject.Entity.Product;

//import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
    @Id
    private Long id;
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    private boolean isCancellable; // No validation needed for boolean fields

    private boolean isReturnable; // No validation needed for boolean fields

    @NotBlank(message = "Brand is required")
    @Size(min = 2, max = 100, message = "Brand must be between 2 and 100 characters")
    private String brand;

    private boolean isActive; // No validation needed for boolean fields

    private boolean isDeleted; // No validation needed for boolean fields

    @ManyToOne
    @JoinColumn(name="Seller_user_id")
    private Seller seller;

    @ManyToOne
    @JoinColumn(name="Category_id")
    private Category category;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
   private  List<ProductVariation> productVariations;

//    @OneToMany(mappedBy="product",cascade = CascadeType.ALL)
//   private  List<ProductReview>productReviews;
}

package com.example.EcommerceProject.EcommerceProject.Entity.Product;

//import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import jakarta.persistence.*;
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
    private Integer id;
    private String name;
    private String description;
    private boolean isCancellable;
    private boolean isReturnable;
    private String brand;
    private boolean isActive;
    private boolean isDeleted;
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

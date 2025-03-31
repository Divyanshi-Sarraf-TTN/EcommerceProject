package com.example.EcommerceProject.EcommerceProject.Entity.Product;


import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ProductReview {
    @NotBlank(message = "Review cannot be empty")
    @Size(min = 10, max = 1000, message = "Review must be between 10 and 1000 characters")
    private String review;

    @NotNull(message = "Rating is required")
    @Pattern(regexp = "^[1-5]$", message = "Rating must be a number between 1 and 5")
    private String rating;


    @ManyToOne
     @MapsId("customerUserId")
    @JoinColumn(name="customerUserId")
    private Customer customer;

     @ManyToOne
     @MapsId("productId")
    @JoinColumn(name="productId")
    private Product product;

     @EmbeddedId
    private ProductReviewID productReviewID;

     public ProductReview(Customer customer,Product product ,String review ,String rating)
     {
         this.customer=customer;
         this.product=product;
         this.review=review;
         this.rating=rating;
         this.productReviewID=new ProductReviewID(customer.getId(), product.getId());
     }
}

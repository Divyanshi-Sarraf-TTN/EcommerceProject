//package com.example.EcommerceProject.EcommerceProject.Entity.Product;
//
//import com.example.EcommerceProject.EcommerceProject.CompositeKey.PRKey;
//import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
//import jakarta.persistence.EmbeddedId;
//import jakarta.persistence.Entity;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@Entity
//public class ProductReview {
//    private String review;
//    private String rating;
//     @ManyToOne
//    @JoinColumn(name="customerUserId")
//    private Customer customer;
//     @ManyToOne
//    @JoinColumn(name="productId")
//    private Product product;
//     @EmbeddedId
//    private PRKey PRKey;
//}

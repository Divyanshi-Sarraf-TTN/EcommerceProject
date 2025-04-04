package com.example.EcommerceProject.EcommerceProject.Entity.User;

import com.example.EcommerceProject.EcommerceProject.Entity.Cart.Cart;
import com.example.EcommerceProject.EcommerceProject.Entity.Product.ProductReview;
//import com.example.EcommerceProject.EcommerceProject.Entity.Cart.Cart;
import com.example.EcommerceProject.EcommerceProject.Entity.Order.Orders;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Customer extends User {
    @NotNull(message = "Contact number is required")
    //@Digits(integer = 10, fraction = 0, message = "Contact number must be exactly 10 digits")
    private Long contact;

    //@NotEmpty(message = "At least one address is required")
    @ManyToMany(mappedBy = "customer",cascade=CascadeType.ALL)
   private List<Address> address;

   @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<ProductReview>productReviews;

    //one to many mapping between customer and order
  @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<Orders>orders;

  @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<Cart> carts;
}

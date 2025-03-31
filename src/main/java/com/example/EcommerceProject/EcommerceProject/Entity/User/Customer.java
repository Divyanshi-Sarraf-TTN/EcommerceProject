package com.example.EcommerceProject.EcommerceProject.Entity.User;

//import com.example.EcommerceProject.EcommerceProject.Entity.Product.ProductReview;
import com.example.EcommerceProject.EcommerceProject.Entity.Cart;
import com.example.EcommerceProject.EcommerceProject.Entity.Order.Orders;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name="user_id")
public class Customer extends User {
    private Long contact;
    @ManyToMany(mappedBy = "customer",cascade=CascadeType.ALL)
   private List<Address> address;

//   @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
//    private List<ProductReview>productReviews;
    //one to many mapping between customer and order
  @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<Orders>orders;
  @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<Cart>carts;
}

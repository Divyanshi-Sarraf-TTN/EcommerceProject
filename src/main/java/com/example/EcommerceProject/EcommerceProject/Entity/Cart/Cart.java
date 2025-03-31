package com.example.EcommerceProject.EcommerceProject.Entity.Cart;

import com.example.EcommerceProject.EcommerceProject.Entity.Product.ProductVariation;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Cart {
    @EmbeddedId
    private CartID cartID;
   private Integer quantity;
   private Boolean isWishListItem;

    @ManyToOne
    @MapsId("productVariationId")
    @JoinColumn(name="productVariationId")
    private ProductVariation productVariation;

    @ManyToOne
    @MapsId("customerUserId")
    @JoinColumn(name="customerUserId")
    private Customer customer;

    public Cart(Customer customer,ProductVariation productVariation,Integer quantity)
    {
        this.customer=customer;
        this.productVariation=productVariation;
        this.quantity=quantity;
        this.cartID=new CartID(customer.getId(), productVariation.getId());

    }
}

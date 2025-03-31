package com.example.EcommerceProject.EcommerceProject.Entity.Product;

import com.example.EcommerceProject.EcommerceProject.Entity.Cart;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ProductVariation {
    @Id
    private Integer id;
    private Integer QuantityAvailable;
    private double Price;
    private String PrimaryImageName;
    private boolean isActive;
    @ManyToOne
    @JoinColumn(name="productID")
    private Product product;
    @OneToMany(mappedBy = "productVariation",cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts;
    @OneToMany(mappedBy="productVariation",cascade = CascadeType.ALL)
    private List<Cart>carts;

}

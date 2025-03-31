package com.example.EcommerceProject.EcommerceProject.Entity;

import com.example.EcommerceProject.EcommerceProject.Entity.Product.ProductVariation;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Cart {
    @ManyToOne
    @JoinColumn(name="productionVariation")
    private ProductVariation productVariation;
    @ManyToOne
    @JoinColumn(name="customerUserId")
    private Customer customer;
}

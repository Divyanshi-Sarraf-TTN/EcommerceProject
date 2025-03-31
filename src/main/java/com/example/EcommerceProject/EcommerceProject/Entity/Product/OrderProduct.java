package com.example.EcommerceProject.EcommerceProject.Entity.Product;

import com.example.EcommerceProject.EcommerceProject.Entity.Order.Orders;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderProduct {
    @Id
    private Integer id;
    private Integer quantity;
    private Integer price;
    @ManyToOne
    @JoinColumn(name="orderId")
    private Orders order;
    @ManyToOne
    @JoinColumn(name="productVariationId")
    private ProductVariation productVariation;
    @OneToOne(mappedBy = "orderProduct",cascade = CascadeType.ALL)
    private OrderStatus orderStatus;
}

package com.example.EcommerceProject.EcommerceProject.Entity.Order;

import com.example.EcommerceProject.EcommerceProject.Entity.Product.ProductVariation;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Integer price;

    @ManyToOne
    @JoinColumn(name="orderId")
    private Orders order;

    @ManyToOne
    @JoinColumn(name="productVariationId")
    private ProductVariation productVariation;

    @OneToMany(mappedBy = "orderProduct",cascade = CascadeType.ALL)
    private List<OrderStatus> orderStatus;
}

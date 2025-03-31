package com.example.EcommerceProject.EcommerceProject.Entity.Product;

//import com.example.EcommerceProject.EcommerceProject.Entity.Cart.Cart;
import com.example.EcommerceProject.EcommerceProject.Entity.Cart.Cart;
import com.example.EcommerceProject.EcommerceProject.Entity.Order.OrderProduct;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    private Long id;
    @NotNull(message = "Quantity Available is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer QuantityAvailable;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.1", inclusive = true, message = "Price must be greater than 0")
    private double Price;

    @NotBlank(message = "Primary Image Name is required")
    @Size(min = 3, max = 255, message = "Image name must be between 3 and 255 characters")
    private String PrimaryImageName;

    private boolean isActive; // No validation needed for boolean fields

    @ManyToOne
    @JoinColumn(name="productID")
    private Product product;
    @OneToMany(mappedBy = "productVariation",cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts;

    @OneToMany(mappedBy="productVariation",cascade = CascadeType.ALL)
    private List<Cart>carts;

}

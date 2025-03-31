package com.example.EcommerceProject.EcommerceProject.Entity.Order;

import com.example.EcommerceProject.EcommerceProject.Entity.Product.OrderProduct;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity

public class Orders {
    @Id
    private Integer id;
    private Double  amountPaid;
    private LocalDate dateCreated;
    private  String paymentMethod;
    private String customerAddressCity;
    private String customerAddressState;
    private String customerAddressCountry;
    private String customerAddressAddressLine;
    private Double customerAddressZipCode;
    private String customerAddressLabel;
    @ManyToOne
    @JoinColumn(name="customerUserId")
    private Customer customer;
    //one to many mapping with order and order product
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    List<OrderProduct>orderProducts;
}

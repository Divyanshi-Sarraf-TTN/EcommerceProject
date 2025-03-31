package com.example.EcommerceProject.EcommerceProject.Entity.User;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private Integer zipCode;
    private String label;
    @OneToOne
    @JoinColumn(name="seller_id")
    private Seller seller;
    @ManyToMany
    @JoinTable(
            name = "customer_address", // Junction table
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")

    )
   private  List<Customer> customer;



}

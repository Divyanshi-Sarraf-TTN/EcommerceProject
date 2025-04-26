package com.example.EcommerceProject.EcommerceProject.Entity.User;


import com.example.EcommerceProject.EcommerceProject.Entity.Audit.AuditEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Address extends AuditEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

   // @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String city;

   // @NotBlank(message = "State is required")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    private String state;

    //@NotBlank(message = "Country is required")
    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    private String country;

    //@NotBlank(message = "Address line is required")
    @Size(min = 5, max = 255, message = "Address line must be between 5 and 255 characters")
    private String addressLine;

    //@NotNull(message = "Zip code is required")
    @Digits(integer = 6, fraction = 0, message = "Zip code must be exactly 6 digits")
    private Integer zipCode;

    //@NotBlank(message = "Label is required")
    @Size(min = 3, max = 20, message = "Label must be between 3 and 20 characters (e.g., Home, Work)")
    private String label;
    public Boolean isDeleted=false;




    @OneToOne
    @JoinColumn(name="seller_id")
    @JsonIgnore
    private Seller seller;

    @ManyToMany
    @JoinTable(
            name = "customer_address", // Junction table
            joinColumns = @JoinColumn(name = "address_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")

    )
   private List<Customer> customers=new ArrayList<>();



}

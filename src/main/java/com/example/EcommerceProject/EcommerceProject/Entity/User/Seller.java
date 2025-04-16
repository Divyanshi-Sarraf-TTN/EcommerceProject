package com.example.EcommerceProject.EcommerceProject.Entity.User;

//import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

public class Seller extends User {
    @NotNull(message = "GST number is required")

    private String gst;

    @NotNull(message = "Company contact is required")
    @Digits(integer = 10, fraction = 0, message = "Company contact must be a valid 10-digit number")
    private Long companyContact;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String companyName;

    @OneToOne(mappedBy = "seller",cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(mappedBy = "seller",cascade = CascadeType.ALL)
    private List<Product> Product;
}

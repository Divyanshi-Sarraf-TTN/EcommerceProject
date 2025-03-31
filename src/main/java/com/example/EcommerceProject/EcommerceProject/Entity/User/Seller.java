package com.example.EcommerceProject.EcommerceProject.Entity.User;

//import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name="user_id")
public class Seller extends User {
    private Double gst;
    private Long companyContact;
    private String companyName;
    @OneToOne(mappedBy = "seller",cascade = CascadeType.ALL)
    private Address address;
//    @OneToMany(mappedBy = "seller",cascade = CascadeType.ALL)
//    private List<Product> Product;
}

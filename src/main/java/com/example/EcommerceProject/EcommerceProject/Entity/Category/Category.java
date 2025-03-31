package com.example.EcommerceProject.EcommerceProject.Entity.Category;

import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    String name;

    @ManyToOne
    @JoinColumn(name="parentCategoryId")
    private Category category;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    private List<Product>products;

//    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
//    private    List<CategoryMetaDataFieldValues> categoryMetaDataFieldValues;
}

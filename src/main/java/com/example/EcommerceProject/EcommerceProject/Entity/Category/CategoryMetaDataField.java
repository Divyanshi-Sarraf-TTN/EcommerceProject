//package com.example.EcommerceProject.EcommerceProject.Entity.Category;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//public class CategoryMetaDataField {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//
//    private Integer id;
//    private String name;
//    @OneToMany(mappedBy = "categoryMetaDataField",cascade = CascadeType.ALL)
//    private List<CategoryMetaDataFieldValues> categoryMetaDataFieldValues;
//}

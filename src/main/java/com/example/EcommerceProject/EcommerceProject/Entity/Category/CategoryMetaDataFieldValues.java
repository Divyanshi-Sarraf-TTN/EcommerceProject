//package com.example.EcommerceProject.EcommerceProject.Entity.Category;
//
//import com.example.EcommerceProject.EcommerceProject.CompositeKey.CMFValues;
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
//public class CategoryMetaDataFieldValues {
//
////    @ElementCollection
////    @CollectionTable(name="values",joinColumns = @JoinColumn(name="values"))
////    @Column(name="tag")
////    private List<String> values;
//
//    @ManyToOne
//    @JoinColumn(name="categoryId")
//    private Category category;
//
//    @ManyToOne
//    @JoinColumn(name="categoryMetadataFieldValues")
//    private CategoryMetaDataField  categoryMetaDataField;
//
//    @EmbeddedId
//    private CMFValues cmfValues;
//
//
//}

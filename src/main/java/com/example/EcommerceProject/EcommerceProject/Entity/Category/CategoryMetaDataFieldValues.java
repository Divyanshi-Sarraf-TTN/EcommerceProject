package com.example.EcommerceProject.EcommerceProject.Entity.Category;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CategoryMetaDataFieldValues {


    private String fieldValues;

    @EmbeddedId
    private CategoryMetaDataFieldValuesID categoryMetaDataFieldValuesID;


    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name="category_id")
    private Category category;

    @ManyToOne
    @MapsId("categoryMetaFieldId")
    @JoinColumn(name="category_meta_field_id")
    private CategoryMetaDataField  categoryMetaDataField;

  public CategoryMetaDataFieldValues(Category category , CategoryMetaDataField categoryMetaDataField , String fieldValues)
  {
      this.category=category;
      this.categoryMetaDataField=categoryMetaDataField;
      this.fieldValues=fieldValues;
      this.categoryMetaDataFieldValuesID= new CategoryMetaDataFieldValuesID(category.getId(), categoryMetaDataField.getId());
  }

}

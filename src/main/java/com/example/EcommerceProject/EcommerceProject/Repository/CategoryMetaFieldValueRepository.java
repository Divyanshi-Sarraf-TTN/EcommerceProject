package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataField;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataFieldValues;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataFieldValuesID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryMetaFieldValueRepository extends JpaRepository<CategoryMetaDataFieldValues,Long> {
    List<CategoryMetaDataFieldValues> findByCategory(Category category);

    boolean existsByCategoryAndCategoryMetaDataField(Category category, CategoryMetaDataField metaField);
    Optional<CategoryMetaDataFieldValues> findByCategoryAndCategoryMetaDataField(Category category, CategoryMetaDataField metaField);

}

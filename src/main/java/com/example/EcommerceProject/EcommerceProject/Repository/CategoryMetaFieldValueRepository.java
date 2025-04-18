package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataFieldValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryMetaFieldValueRepository extends JpaRepository<CategoryMetaDataFieldValues,Long> {
    List<CategoryMetaDataFieldValues> findByCategory(Category category);
}

package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    boolean existsByNameIgnoreCaseAndParentCategoryIsNull(String name);

    boolean existsByNameIgnoreCaseAndParentCategory(String name, Category parentCategory);
    List<Category>findByParentCategory(Category parentCategory);

    Page<Category> findByNameContainingIgnoreCase(String query, Pageable pageable);

    boolean existsByNameIgnoreCaseAndParentCategoryAndIdNot(String name, Category parent, Long currentCategoryId);

    List<Category> findByParentCategoryIsNull();

    boolean existsByParentCategory(Category category);

    List<Category> findByParentCategoryId(Long id);
}

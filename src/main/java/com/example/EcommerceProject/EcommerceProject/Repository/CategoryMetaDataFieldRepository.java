package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CategoryMetaDataFieldRepository extends JpaRepository<CategoryMetaDataField,Long> {
    Optional<CategoryMetaDataField> findByNameIgnoreCase(String name);



    Page<CategoryMetaDataField> findAll(Specification<CategoryMetaDataField> spec, Pageable pageable);
}

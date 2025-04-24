package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByCategory_Id(Long categoryId);

    boolean existsByNameIgnoreCaseAndBrandIgnoreCaseAndCategoryAndSeller(@NotBlank(message = "Product name is required") @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters") String name, @NotBlank(message = "Brand is required") @Size(min = 2, max = 100, message = "Brand must be between 2 and 100 characters") String brand, Category category, Seller seller);

    Page<Product> findBySellerAndIsDeletedFalseAndNameContainingIgnoreCase(Seller seller, String query, Pageable pageable);

    Page<Product> findBySellerAndIsDeletedFalse(Seller seller, Pageable pageable);

    Optional<Object> findByIdAndIsDeletedFalse(Long productId);

    boolean existsByNameAndBrandAndCategoryAndSeller(String name, @NotBlank(message = "Brand is required") @Size(min = 2, max = 100, message = "Brand must be between 2 and 100 characters") String brand, Category category, Seller seller);

    Optional<Product> findByIdAndIsDeletedFalseAndIsActiveTrue(Long productId);

    Page<Product> findAllByCategoryIdInAndIsDeletedFalseAndIsActiveTrue(List<Long> categoryIds, Pageable pageable);
}
package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByCategory_Id(Long categoryId);

    boolean existsByNameIgnoreCaseAndBrandIgnoreCaseAndCategoryAndSeller(@NotBlank(message = "Product name is required") @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters") String name, @NotBlank(message = "Brand is required") @Size(min = 2, max = 100, message = "Brand must be between 2 and 100 characters") String brand, Category category, Seller seller);

    Page<Product> findBySellerAndIsDeletedFalseAndNameContainingIgnoreCase(Seller seller, String query, Pageable pageable);

    Page<Product> findBySellerAndIsDeletedFalse(Seller seller, Pageable pageable);

    Optional<Object> findByIdAndIsDeletedFalse(Long productId);

    boolean existsByNameAndBrandAndCategoryAndSeller(String name, @NotBlank(message = "Brand is required") @Size(min = 2, max = 100, message = "Brand must be between 2 and 100 characters") String brand, Category category, Seller seller);

    Optional<Product> findByIdAndIsDeletedFalseAndIsActiveTrue(Long productId);

    Page<Product> findAllByCategoryIdInAndIsDeletedFalseAndIsActiveTrue(List<Long> categoryIds, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isDeleted = false " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:sellerId IS NULL OR p.seller.id = :sellerId)")
    Page<Product> findFilteredProducts(
            @Param("categoryId") Long categoryId,
            @Param("sellerId") Long sellerId,
            Pageable pageable
    );


    Page<Product> findByCategoryIdAndIdNotAndIsDeletedFalseAndIsActiveTrueAndNameContainingIgnoreCase(Long categoryId, Long productId, String query, Pageable pageable);

    Page<Product> findByCategoryIdAndIdNotAndIsDeletedFalseAndIsActiveTrue(Long categoryId, Long productId, Pageable pageable);
    Page<Product> findByCategoryAndIdNotAndIsDeletedFalseAndIsActiveTrueAndNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
            Category category,
            Long excludedProductId,
            String nameQuery,
            String brandQuery,
            Pageable pageable
    );
    Page<Product> findByCategoryAndIdNotAndIsDeletedFalseAndIsActiveTrue(
            Category category,
            Long excludedProductId,
            Pageable pageable
    );


}
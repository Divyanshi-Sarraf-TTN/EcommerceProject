package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import com.example.EcommerceProject.EcommerceProject.Entity.Product.ProductVariation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariationRepository extends JpaRepository<ProductVariation,Long> {

    List<ProductVariation> findByProduct(Product product);

    List<ProductVariation> findAllByProduct(Product product);

    Page<ProductVariation> findByProductIdAndMetadataContainingIgnoreCase(Long productId, String query, Pageable pageable);

    Page<ProductVariation> findByProductId(Long productId, Pageable pageable);

    List<ProductVariation> findByProductIdAndIsActiveTrue(Long productId);
}

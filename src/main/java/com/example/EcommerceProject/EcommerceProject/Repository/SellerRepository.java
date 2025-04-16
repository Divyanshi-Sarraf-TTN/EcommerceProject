package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller,Long> {
    Optional<Seller>findByEmail(String email);
    Optional<Object>findByGst(String gst);
    Optional<Object>findByCompanyName(String companyName);
    Page<Seller> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    public boolean existsByEmail(String email);

    public boolean existsByGst(String gst);
    public boolean existsByCompanyName(String companyName);
    public boolean existsByCompanyContact(Long companyContact);
}

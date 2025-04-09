package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller,Long> {
    Optional<Object>findByEmail(String email);
    Optional<Object>findByGst(String gst);
    Optional<Object>findByCompanyName(String companyName);
}

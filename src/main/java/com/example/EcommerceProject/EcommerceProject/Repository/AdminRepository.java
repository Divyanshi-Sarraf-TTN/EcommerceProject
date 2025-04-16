package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Admin;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
}

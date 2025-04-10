package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Object> findByEmail(String email);
    Optional<Object>findByContact(Long contact);
    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);

}

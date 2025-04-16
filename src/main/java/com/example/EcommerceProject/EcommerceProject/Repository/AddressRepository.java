package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {
    @Query("SELECT a FROM Address a WHERE a.city = :city AND a.state = :state AND a.country = :country " +
            "AND a.addressLine = :addressLine AND a.zipCode = :zipCode AND a.label = :label")
    Optional<Address> findByAllFields(String city, String state, String country, String addressLine, Integer zipCode, String label);

}

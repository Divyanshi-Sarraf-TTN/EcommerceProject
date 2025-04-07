package com.example.EcommerceProject.EcommerceProject.Repository;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByAuthority(String authority);
}

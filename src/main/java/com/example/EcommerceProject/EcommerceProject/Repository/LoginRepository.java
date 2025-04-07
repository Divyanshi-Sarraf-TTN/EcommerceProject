//package com.example.EcommerceProject.EcommerceProject.Repository;
//
//import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//
//import java.util.Optional;
//
//public interface LoginRepository extends JpaRepository<User,Long> {
//    Optional<Object>findByEmail(String email);
//
//    Optional<Object> findByPassword( String password);
//}

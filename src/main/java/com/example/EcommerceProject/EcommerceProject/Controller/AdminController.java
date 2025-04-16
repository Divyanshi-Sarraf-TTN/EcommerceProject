package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.DTO.CustomerResponseDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.SellerResponseDTO;
import com.example.EcommerceProject.EcommerceProject.Service.AdminService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<CustomerResponseDTO>> getAllCustomers(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int pageOffset,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "") String email
    ) {
        Pageable pageable = PageRequest.of(
                pageOffset,
                pageSize,
                sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );
        return ResponseEntity.ok(adminService.getAllCustomers(email, pageable));
    }
    @GetMapping("/sellers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<SellerResponseDTO>> getAllSellers(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int pageOffset,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "") String email
    ) {
        Pageable pageable = PageRequest.of(
                pageOffset,
                pageSize,
                sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );
        return ResponseEntity.ok(adminService.getAllSellers(email, pageable));
    }
   @PatchMapping("/user/activate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String>customerActivate(@RequestParam Long id)throws MessagingException{
        return ResponseEntity.ok(adminService.activateUser(id));
   }
   @PatchMapping("/user/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String>deactivateCustomer(@RequestParam Long id)throws MessagingException{
        return ResponseEntity.ok(adminService.deactivateUser(id));
   }
//    @PatchMapping("/sellers/activate")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<String>sellerActivate(@RequestParam Long id)throws MessagingException{
//        return ResponseEntity.ok(adminService.activateUser(id));
//    }
//    @PatchMapping("/sellers/deactivate")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<String>deactivateSeller(@RequestParam Long id)throws MessagingException{
//        return ResponseEntity.ok(adminService.deactivateUser(id));
//    }

}

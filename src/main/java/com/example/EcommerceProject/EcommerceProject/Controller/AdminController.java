package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.DTO.*;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataField;
import com.example.EcommerceProject.EcommerceProject.Service.AdminService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<String> customerActivate(@RequestParam Long id) throws MessagingException {
        return ResponseEntity.ok(adminService.activateUser(id));
    }

    @PatchMapping("/user/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deactivateCustomer(@RequestParam Long id) throws MessagingException {
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
    @PostMapping("/metadatafield")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addMetaDataField(@Valid @RequestBody CategoryMetaDataField field) {
        if(field.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Name is required");
        }


        return ResponseEntity.ok(adminService.addMetaDataField(field));
    }

    @GetMapping("/allmetadatafields")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<CategoryMetaDataField> getAllMetadataFields(
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String query
    ) {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));
        Map<String, Object> filters = new HashMap<>();
        //in query =name:color,id:152
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":");
                if (kv.length == 2)
                    filters.put(kv[0].trim(), kv[1].trim());
            }
        }
        //filters={
        // name:color,
        //id:152
        //}
        return adminService.getAllMetaDataFields(filters, pageable);


    }
    @PostMapping("/addcategories")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String>addCategory(@Valid @RequestBody CategoryRequestDTO dto)
    {

        return ResponseEntity.ok(adminService.addCategory(dto));
    }
    @GetMapping("/viewcategory")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ViewCategoryResponse> viewCategory(@RequestParam Long id) {
        ViewCategoryResponse response = adminService.getOneCategory(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allcategories")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ViewCategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String query) {

        List<ViewCategoryResponse> response = adminService.getAllCategories(offset, max, sort, order, query);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/updatecategory")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody UpdateCategoryRequestDTO dto) {
        return ResponseEntity.ok(adminService.updateCategory(dto));
    }
    @PostMapping("/categorymetadatavalues")
    public ResponseEntity<String> addMetadataValues(@RequestBody CategoryMetaDataFieldValueRequest request)
    {
        adminService.addCategoryMetaDataFieldValues(request);
        return ResponseEntity.ok("Category metadata field values added successfully!");
    }
    @PutMapping("/updatecategorymetadatavalues")
    public ResponseEntity<String> updateMetadataValues(@RequestBody CategoryMetaDataFieldValueRequest request)
    {
        adminService.updateCategoryMetaDataFieldValues(request);
        return ResponseEntity.ok("Category metadata field values updated successfully!");
    }



}

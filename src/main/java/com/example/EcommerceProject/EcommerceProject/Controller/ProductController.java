package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.DTO.AddProductRequest;
import com.example.EcommerceProject.EcommerceProject.DTO.ProductResponse;
import com.example.EcommerceProject.EcommerceProject.DTO.UpdateProductRequest;
import com.example.EcommerceProject.EcommerceProject.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService productService;
    @PostMapping("/addproduct")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<String> addProduct(@Valid @RequestBody AddProductRequest request){
        String response =productService.createProduct(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/viewaproduct")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ProductResponse viewProduct(@RequestParam Long id) {
        return productService.viewProduct(id);
    }
    @GetMapping("/viewallproducts")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")

    public ResponseEntity<List<ProductResponse>> getAllSellerProducts(
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String query
    ) {
        List<ProductResponse> products = productService.getAllProductsBySeller(max, offset, sort, order, query);
        return ResponseEntity.ok(products);
    }
    @DeleteMapping("/deleteaproduct")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<String> deleteProduct(@RequestParam Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully.");
    }
    @PutMapping("/updateaproduct")
    public ResponseEntity<String> updateProduct(
            @RequestParam Long id,
            @RequestBody UpdateProductRequest updateRequest
    ) {
        productService.updateProduct(id, updateRequest);
        return ResponseEntity.ok("Product updated successfully.");
    }

}

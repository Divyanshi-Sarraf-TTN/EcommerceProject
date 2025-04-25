package com.example.EcommerceProject.EcommerceProject.Controller;

import com.example.EcommerceProject.EcommerceProject.DTO.*;
import com.example.EcommerceProject.EcommerceProject.Service.ProductService;
import com.example.EcommerceProject.EcommerceProject.Utils.JsonUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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




        @PostMapping("/addproductvariation")
        @PreAuthorize("hasAuthority('ROLE_SELLER')")
        public ResponseEntity<String> addProductVariation(@ModelAttribute ProductVariationDTO dto) {
            Map<String,Object> metadata = JsonUtil.parseJsonToMap(dto.getMetadata());
            Long variationId = productService.addVariation(dto,metadata);
            return ResponseEntity.ok("product variation added");
        }
    @GetMapping("viewaproductvariation")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ProductVariationResponseDTO> getProductVariation(@RequestParam Long id) {
        ProductVariationResponseDTO response = productService.getVariationById(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/viewallvariations")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<Page<ProductVariationResponseDTO>> viewAllVariations(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String query
    ) {
        Page<ProductVariationResponseDTO> response = productService.getAllVariationsByProductId(
                productId, offset, max, sort, order, query
        );
        return ResponseEntity.ok(response);
    }
    @PutMapping("/updateproductvariaton")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<String> updateProductVariation(@RequestParam Long id,@ModelAttribute UpdateProductVariationRequest request)
    {
        Map<String,Object> metadata = JsonUtil.parseJsonToMap(request.getMetadata());
        productService.updateProductVariation(id,request,metadata);
        return ResponseEntity.ok("Product variation updated successfully.");
    }

    @GetMapping("/viewaproductbycustomer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")

    public ResponseEntity<ProductViewResponse> viewProductByCustomer(@RequestParam Long id) {
        return ResponseEntity.ok(productService.viewAProductByCustomer(id));
    }
    @GetMapping("viewallproductsbycustomer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Page<ProductViewResponse>> getProductsByCategory(@RequestParam Long id,


      @RequestParam(defaultValue = "0") int offset,
    @RequestParam(defaultValue = "10") int max,
    @RequestParam(defaultValue = "id") String sort,
    @RequestParam(defaultValue = "asc") String order,
    @RequestParam(required = false) String query
    ) {
        Page<ProductViewResponse> pageResponse = productService.getProductsByCategoryId(id, offset, max, sort, order, query);
        return ResponseEntity.ok(pageResponse);
    }
    //TO GET APRODUCT BY ADMIN
    @GetMapping("/viewaproductbyadmin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<ProductViewResponse> viewProductByAdmin(@RequestParam Long id) {
        return ResponseEntity.ok(productService.viewAProductByAdmin(id));
    }
    @PutMapping("/deactivateproduct")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deactivateProduct(@RequestParam Long ProductId) {

        return ResponseEntity.ok(productService.deActivateProduct(ProductId));
    }
    @PutMapping("/activateproduct")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> activateProduct(@RequestParam Long ProductId) {

        return ResponseEntity.ok(productService.activateProduct(ProductId));
    }
    //get all productsbyadmin
    @GetMapping("/viewallproductsbyadmin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<AllProductResponse>> getAllProductsByAdmin(
            @RequestParam(defaultValue = "10") Integer max,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sellerId
    ) {
        Page<AllProductResponse> products = productService.getAllActiveProductsByAdmin(max, offset, sort, order, categoryId, sellerId);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/viewsimilarproducts")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Page<ProductSummaryDTO>> getSimilarProducts(@RequestParam Long productId,
                                                                      @RequestParam(defaultValue = "10") int max,
                                                                      @RequestParam(defaultValue = "0") int offset,
                                                                      @RequestParam(defaultValue = "id") String sort,
                                                                      @RequestParam(defaultValue = "asc") String order,
                                                                      @RequestParam(required = false) String query
    ) {
        Page<ProductSummaryDTO> response = productService.getSimilarProducts(productId, max, offset, sort, order, query);
        return ResponseEntity.ok(response);
    }
}






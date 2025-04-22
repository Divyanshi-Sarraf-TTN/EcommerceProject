package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.AddProductRequest;
import com.example.EcommerceProject.EcommerceProject.DTO.ProductResponse;
import com.example.EcommerceProject.EcommerceProject.DTO.UpdateProductRequest;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import com.example.EcommerceProject.EcommerceProject.Exception.ForbiddenAccessException;
import com.example.EcommerceProject.EcommerceProject.Exception.ResourceNotFoundException;
import com.example.EcommerceProject.EcommerceProject.Repository.CategoryRepository;
import com.example.EcommerceProject.EcommerceProject.Repository.ProductRepository;
import com.example.EcommerceProject.EcommerceProject.Repository.SellerRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class ProductService {
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EmailService emailService;

    public String createProduct(AddProductRequest req) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // 1) leaf-node check
        if (categoryRepository.existsByParentCategory(category)) {
            throw new ForbiddenAccessException("Category must be a leaf node (no subcategories)");
        }

        // 2) uniqueness check
        boolean exists = productRepository.existsByNameIgnoreCaseAndBrandIgnoreCaseAndCategoryAndSeller(
                req.getName(), req.getBrand(), category, seller);
        if (exists) {
            throw new ForbiddenAccessException(
                    "Product with the same name, brand, and category already exists for this seller");
        }

        // 3) build & save
        Product product = new Product();
        product.setName(req.getName());
        product.setBrand(req.getBrand());
        product.setDescription(req.getDescription());
        product.setCategory(category);
        product.setSeller(seller);
        product.setCancellable(Optional.ofNullable(req.getIsCancellable()).orElse(false));
        product.setReturnable(Optional.ofNullable(req.getIsReturnable()).orElse(false));
        product.setDeleted(false);
        product.setActive(false); // inactive by default

        Product saved = productRepository.save(product);

        // Optional: notify admin
        emailService.sendProductActivateToAdmin(saved);

        return "Product created with ID: " + saved.getId();
    }

    public ProductResponse viewProduct(Long productId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new ForbiddenAccessException("You are not authorized to view this product");
        }

        if (product.isDeleted()) {
            throw new ResourceNotFoundException("Product not found (deleted)");
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.isCancellable(),
                product.isReturnable(),
                product.isActive(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
    public List<ProductResponse> getAllProductsBySeller(int max, int offset, String sort, String order, String query) {
        // Get the logged-in seller
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Define sorting and pagination
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));

        // Fetch products
        Page<Product> productPage;
        if (query != null && !query.trim().isEmpty()) {
            productPage = productRepository.findBySellerAndIsDeletedFalseAndNameContainingIgnoreCase(
                    seller, query, pageable);
        } else {
            productPage = productRepository.findBySellerAndIsDeletedFalse(seller, pageable);
        }

        // Convert each Product to ProductResponse using a for loop
        List<ProductResponse> responseList = new ArrayList<>();
        for (Product product : productPage.getContent()) {
            ProductResponse response = new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getBrand(),
                    product.isCancellable(),
                    product.isReturnable(),
                    product.isActive(),
                    product.getCategory().getId(),
                    product.getCategory().getName()
            );
            responseList.add(response);
        }

        return responseList;
    }
    public void deleteProduct(Long productId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new ForbiddenAccessException("You are not authorized to delete this product");
        }

        product.setDeleted(true);
        productRepository.save(product);
    }
    public void updateProduct(Long productId, UpdateProductRequest request) {
        // Get logged-in seller
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Fetch product by ID
        Product product = (Product) productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check ownership
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new ForbiddenAccessException("You are not allowed to update this product");
        }
         if(request.getName().equalsIgnoreCase(product.getName()))
         {
             throw  new ForbiddenAccessException("Product name already exist");
         }

        // Optional update: name
        if (request.getName() != null && !request.getName().equalsIgnoreCase(product.getName())) {
            boolean exists = productRepository.existsByNameAndBrandAndCategoryAndSeller(
                    request.getName(), product.getBrand(), product.getCategory(), seller);
            if (exists) {
                throw new ValidationException("Product name must be unique for the brand, category, and seller");
            }
            product.setName(request.getName());
        }

        // Optional updates
        if (request.getDescription() != null)
            product.setDescription(request.getDescription());

        if (request.getIsCancellable() != null)
            product.setCancellable(request.getIsCancellable());

        if (request.getIsReturnable() != null)
            product.setReturnable(request.getIsReturnable());

        // Save updated product
        productRepository.save(product);
    }




}

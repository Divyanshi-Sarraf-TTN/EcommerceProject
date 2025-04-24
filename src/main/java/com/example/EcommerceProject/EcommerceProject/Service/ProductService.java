package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.*;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataFieldValues;
import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import com.example.EcommerceProject.EcommerceProject.Entity.Product.ProductVariation;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import com.example.EcommerceProject.EcommerceProject.Exception.ForbiddenAccessException;
import com.example.EcommerceProject.EcommerceProject.Exception.MethodNotAllowedException;
import com.example.EcommerceProject.EcommerceProject.Exception.ResourceNotFoundException;
import com.example.EcommerceProject.EcommerceProject.Repository.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;

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
    @Autowired
    private CategoryMetaFieldValueRepository categoryMetaFieldValueRepository;
    @Autowired
    private VariationRepository variationRepository;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${image.upload.path}")
    private String imageUploadPath;

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
    //add variation by seller
    public Long addVariation(ProductVariationDTO dto, Map<String,Object> metadata) {
        String sellerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new ForbiddenAccessException("You are not the owner of this product.");
        }

        if (!product.isActive() || product.isDeleted()) {
            throw new MethodNotAllowedException("Product must be active and not deleted");
        }

        if (dto.getQuantityAvailable() < 0) {
            throw new ValidationException("Quantity must be 0 or more");
        }
        System.out.println("Quantity: " + dto.getQuantityAvailable());


        if (dto.getPrice() < 0) {
            throw new ValidationException("Price must be 0 or more");
        }

        if (metadata == null || metadata.isEmpty()) {
            throw new ValidationException("At least one metadata field-value pair is required");
        }
        List<ProductVariation> variationList = variationRepository.findAllByProduct(product);
        for (ProductVariation productVariation : variationList) {
            System.out.println("Existing Metadata: " + productVariation.getMetadata());
            System.out.println("Submitted Metadata: " + metadata);
            if (metadata.equals(productVariation.getMetadata())) {
                throw new ForbiddenAccessException("Metadata already exists");
            }
        }

        Category category = product.getCategory();
        List<CategoryMetaDataFieldValues> allowedFieldValues = categoryMetaFieldValueRepository.findByCategory(category);

        Map<String, Set<String>> allowedMap = new HashMap<>();
        for (CategoryMetaDataFieldValues cmfv : allowedFieldValues) {
            allowedMap.put(cmfv.getCategoryMetaDataField().getName(),
                    new HashSet<>(Arrays.asList(cmfv.getFieldValues().split(","))));
        }

        Set<String> submittedFields = metadata.keySet();
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            if (!allowedMap.containsKey(entry.getKey()) ||
                    !allowedMap.get(entry.getKey()).contains(entry.getValue())) {
                System.out.println(allowedMap.toString());
                throw new ValidationException("Invalid metadata field or value: " + entry.getKey());
            }
        }

        List<ProductVariation> existing = variationRepository.findByProduct(product);
        if (!existing.isEmpty()) {
            Set<String> structure = existing.get(0).getMetadata().keySet();
            if (!structure.equals(submittedFields)) {
                throw new ValidationException("Metadata structure must match existing variations");
            }
        }

        if (!isValidImage(dto.getPrimaryImage())) {
            throw new ValidationException("Invalid primary image format");
        }

        String primaryImageName = storePrimaryImage(dto.getPrimaryImage(), product.getId());

        ProductVariation variation = new ProductVariation();
        variation.setProduct(product);
        variation.setPrice(dto.getPrice());
        variation.setQuantityAvailable(dto.getQuantityAvailable());
        variation.setPrimaryImageName(primaryImageName);
        variation.setActive(true);
        variation.setMetadata(metadata);

        return variationRepository.save(variation).getId();
    }

    private boolean isValidImage(MultipartFile file) {
        String type = file.getContentType();
        return type != null && type.matches("image/(jpeg|jpg|png|bmp)");
    }

//view aproductvariation by seller
    public ProductVariationResponseDTO getVariationById(Long id) {
        String sellerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        ProductVariation variation = variationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product variation not found"));

        Product product = variation.getProduct();

        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new ForbiddenAccessException("You are not authorized to view this variation.");
        }

        if (product.isDeleted()) {
            throw new MethodNotAllowedException("Parent product is deleted.");
        }

        ProductVariationResponseDTO dto = new ProductVariationResponseDTO();
        dto.setId(variation.getId());
        dto.setPrice(variation.getPrice());
        dto.setQuantityAvailable(variation.getQuantityAvailable());
        dto.setPrimaryImageUrl(variation.getPrimaryImageName());
        dto.setActive(variation.isActive());
        dto.setMetadata(variation.getMetadata());

        ProductResponseDTO productDTO = new ProductResponseDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setBrand(product.getBrand());
        productDTO.setActive(product.isActive());

        dto.setProduct(productDTO);

        return dto;
    }
//view allproductvariation
    public Page<ProductVariationResponseDTO> getAllVariationsByProductId(Long productId, int offset, int max,
                                                                         String sortField, String sortOrder,
                                                                         String query) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.isDeleted()) {
            throw new RuntimeException("Product is deleted.");
        }

        if (!product.getSeller().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to view variations for this product.");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
        Pageable pageable = PageRequest.of(offset, max, sort);

        Page<ProductVariation> variations;
        if (query != null && !query.isBlank()) {
            variations = variationRepository.findByProductIdAndMetadataContainingIgnoreCase(productId, query, pageable);
        } else {
            variations = variationRepository.findByProductId(productId, pageable);
        }

        return variations.map(variation -> {
            ProductVariationResponseDTO dto = new ProductVariationResponseDTO();
            dto.setId(variation.getId());
            dto.setActive(variation.isActive());
            dto.setMetadata(variation.getMetadata());
            dto.setPrice(variation.getPrice());
            dto.setQuantityAvailable(variation.getQuantityAvailable());
            dto.setPrimaryImageUrl(variation.getPrimaryImageName());

            ProductResponseDTO productDTO = new ProductResponseDTO();
            productDTO.setId(product.getId());
            productDTO.setName(product.getName());
            productDTO.setBrand(product.getBrand());
            productDTO.setActive(product.isActive());

            dto.setProduct(productDTO);
            return dto;
        });

    }

//updateproductvariation
    public void updateProductVariation(Long id, UpdateProductVariationRequest request, Map<String, Object> metadata) {
        // Get seller email from security context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Get seller entity
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenAccessException("Seller not found"));

        // Get variation and verify ownership
        ProductVariation variation = variationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product variation not found"));

        if (!variation.getProduct().getSeller().getId().equals(seller.getId())) {
            throw new ForbiddenAccessException("You are not authorized to update this variation");
        }

        // Ensure product is active and not deleted
        if (variation.getProduct().isDeleted() || !variation.getProduct().isActive()) {
            throw new ValidationException("Product must be active and not deleted");
        }


        // Validate metadata structure and values
        if (request.getMetadata() != null) {
            Map<String, Object> submittedMetadata = metadata;

            // Get category and allowed metadata fields
            Category category = variation.getProduct().getCategory();
            List<CategoryMetaDataFieldValues> allowedMetaFields =
                    categoryMetaFieldValueRepository.findByCategory(category);

            Map<String, List<String>> allowedMap = allowedMetaFields.stream()
                    .collect(Collectors.toMap(
                            f -> f.getCategoryMetaDataField().getName(),
                            f -> List.of(f.getFieldValues().split(",\\s*"))
                    ));

            for (Map.Entry<String, Object> entry : submittedMetadata.entrySet()) {
                String field = entry.getKey();
                String value = String.valueOf(entry.getValue());

                if (!allowedMap.containsKey(field)) {
                    throw new ValidationException("Field '" + field + "' is not allowed for this category.");
                }

                if (!allowedMap.get(field).contains(value)) {
                    throw new ValidationException("Value '" + value + "' for field '" + field +
                            "' is invalid. Allowed values: " + allowedMap.get(field));
                }
            }

            variation.setMetadata(submittedMetadata);
        }

        // Update fields if present
        if (request.getQuantityAvailable() != null) {
            variation.setQuantityAvailable(request.getQuantityAvailable());
        }

        if (request.getPrice() != null) {
            variation.setPrice(request.getPrice());
        }
        String primaryImageName = storePrimaryImage(request.getPrimaryImageName(), variation.getId());

        if (request.getPrimaryImageName() != null) {
            variation.setPrimaryImageName(primaryImageName);
        }

        if (request.getIsActive() != null) {
            variation.setActive(request.getIsActive());
        }

        variationRepository.save(variation);
    }

    private String storePrimaryImage(MultipartFile image, Long productId) {
        String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID() + extension;
        String path = "/products/" + productId + "/variations/" + filename; // absolute path on your machine/server

        Path fullPath = Paths.get(imageUploadPath + "/products/" + productId + "/variations/" + filename);
        try {
            File file = new File(String.valueOf(fullPath)); // fullPath = final path including file name

            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs(); // creates the necessary directory structure
            }


            System.out.println(fullPath);

            Files.copy(image.getInputStream(), fullPath);

            return baseUrl + path;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }
    //viewaproductbycustomer
    public ProductViewResponse viewAProductByCustomer(Long productId) {
        Product product = productRepository.findByIdAndIsDeletedFalseAndIsActiveTrue(productId)
                .orElseThrow(() -> new RuntimeException("Product not found, inactive or deleted"));

        List<ProductVariation> activeVariations = variationRepository
                .findByProductIdAndIsActiveTrue(productId);

        if (activeVariations.isEmpty()) {
            throw new RuntimeException("No active product variations available");
        }

        ProductViewResponse response = new ProductViewResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setBrand(product.getBrand());
        response.setIsCancellable(product.isCancellable());
        response.setIsReturnable(product.isReturnable());
        response.setIsActive(product.isActive());

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(product.getCategory().getId());
        categoryResponse.setName(product.getCategory().getName());
        response.setCategory(categoryResponse);

        List<ProductVariationResponse> variationResponses = activeVariations.stream().map(variation -> {
            ProductVariationResponse variationResponse = new ProductVariationResponse();
            variationResponse.setId(variation.getId());
            variationResponse.setPrice(variation.getPrice());
            variationResponse.setQuantityAvailable(variation.getQuantityAvailable());
            variationResponse.setMetadata(variation.getMetadata());
            variationResponse.setPrimaryImageName(variation.getPrimaryImageName());
            return variationResponse;
        }).collect(Collectors.toList());

        response.setProductVariations(variationResponses);

        return response;
    }
    //viewallproductsbycustomer
    public Page<ProductViewResponse> getProductsByCategoryId(Long categoryId, int offset, int max,
                                                             String sortField, String sortOrder,
                                                             String query) {

        // Step 1: Validate category existence
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        // Step 2: Determine leaf categories
        List<Long> leafCategoryIds;
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            leafCategoryIds = List.of(category.getId());
        } else {
            leafCategoryIds = getLeafCategoryIds(category);
            if (leafCategoryIds.isEmpty()) {
                throw new ForbiddenAccessException("No valid sub-categories found.");
            }
        }

        // Step 3: Prepare Pageable object
        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(offset, max, sort);

        // Step 4: Fetch paginated active, non-deleted products
        Page<Product> productPage = productRepository
                .findAllByCategoryIdInAndIsDeletedFalseAndIsActiveTrue(leafCategoryIds, pageable);

        if (productPage.isEmpty()) {
            throw new ResourceNotFoundException("No products found for the given category.");
        }

        // Step 5: Map Page<Product> -> Page<ProductViewResponse>
        Page<ProductViewResponse> responsePage = productPage.map(product -> {
            // Extract active variations
            List<ProductVariationResponse> variationDTOList = product.getProductVariations().stream()
                    .filter(variation -> Boolean.TRUE.equals(variation.isActive()))
                    .map(variation -> ProductVariationResponse.builder()
                            .id(variation.getId())
                            .primaryImageName(variation.getPrimaryImageName())
                            .price(variation.getPrice())
                            .quantityAvailable(variation.getQuantityAvailable())
                            .metadata(variation.getMetadata())
                            .build())
                    .toList();

            // Build category DTO
            CategoryResponse categoryDTO = CategoryResponse.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .build();

            // Build product response
            return ProductViewResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .brand(product.getBrand())
                    .isCancellable(product.isCancellable())
                    .isReturnable(product.isReturnable())
                    .category(categoryDTO)
                    .productVariations(variationDTOList)
                    .build();
        });

        return responsePage;
    }


    private List<Long> getLeafCategoryIds(Category category) {
        List<Long> leafCategoryIds = new ArrayList<>();

        // First, check if this category has products directly
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            // If the category has products, it's a leaf category
            leafCategoryIds.add(category.getId());
        } else {
            // If there are no products, check for subcategories
            List<Category> subCategories = categoryRepository.findByParentCategoryId(category.getId());

            // If there are no subcategories, this category is considered a leaf
            if (subCategories.isEmpty()) {
                leafCategoryIds.add(category.getId());
            } else {
                // Otherwise, recursively find leaf categories from subcategories
                for (Category subCategory : subCategories) {
                    leafCategoryIds.addAll(getLeafCategoryIds(subCategory));  // Recursive call for subcategories
                }
            }
        }

        return leafCategoryIds;
    }
    public ProductViewResponse viewAProductByAdmin(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id"));


        List<ProductVariation> variations = product.getProductVariations();
        ProductViewResponse response = new ProductViewResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setBrand(product.getBrand());
        response.setIsCancellable(product.isCancellable());
        response.setIsReturnable(product.isReturnable());
        response.setIsActive(product.isActive());

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(product.getCategory().getId());
        categoryResponse.setName(product.getCategory().getName());
        response.setCategory(categoryResponse);

        List<ProductVariationResponse> variationResponses = variations.stream().map(variation -> {
            ProductVariationResponse variationResponse = new ProductVariationResponse();
            variationResponse.setId(variation.getId());
            variationResponse.setPrice(variation.getPrice());
            variationResponse.setQuantityAvailable(variation.getQuantityAvailable());
            variationResponse.setMetadata(variation.getMetadata());
            variationResponse.setPrimaryImageName(variation.getPrimaryImageName());
            return variationResponse;
        }).collect(Collectors.toList());

        response.setProductVariations(variationResponses);

        return response;
    }

    public String activateProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("NO product with this id"));
        if (!product.isActive()) {
            product.setActive(true);
        }
        else{
            throw new ValidationException("Product is already Activated");
        }
        emailService.sendProductActivationUpdateToSeller(product);
        productRepository.save(product);
        return "Activate Successfully";
    }


    public String deActivateProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("NO product with this id"));
        if (product.isActive()) {
            product.setActive(false);
        }
        else{
            throw new ValidationException("Product is already De-Activated");
        }

        emailService.sendProductDeActivationUpdateToSeller(product);
        productRepository.save(product);
        return "De-Activate Successfully";
    }

    }




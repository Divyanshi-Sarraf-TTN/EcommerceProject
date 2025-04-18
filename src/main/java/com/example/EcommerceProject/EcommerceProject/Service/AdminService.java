package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.*;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataField;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataFieldValues;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import com.example.EcommerceProject.EcommerceProject.Exception.ForbiddenAccessException;
import com.example.EcommerceProject.EcommerceProject.Exception.MethodNotAllowedException;
import com.example.EcommerceProject.EcommerceProject.Exception.ResourceNotFoundException;
import com.example.EcommerceProject.EcommerceProject.Exception.UserNotFoundException;
import com.example.EcommerceProject.EcommerceProject.Repository.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CategoryMetaDataFieldRepository categoryMetaDataFieldRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryMetaFieldValueRepository categoryMetaFieldValueRespository;

    public Page<CustomerResponseDTO> getAllCustomers(String emailFilter, Pageable pageable) {
        logger.info("Fetching all customers with email filter: {}", emailFilter);
        Page<Customer> customers = customerRepository.findByEmailContainingIgnoreCase(emailFilter, pageable);
        logger.debug("Found {} customers", customers.getTotalElements());
        return customers.map(c -> new CustomerResponseDTO(
                c.getId(),
                c.getFirstName() + " " + c.getLastName(),
                c.getEmail(),
                c.isActive()
        ));
    }

    public Page<SellerResponseDTO> getAllSellers(String emailFilter, Pageable pageable) {
        logger.info("Fetching all sellers with email filter: {}", emailFilter);
        Page<Seller> sellers = sellerRepository.findByEmailContainingIgnoreCase(emailFilter, pageable);
        logger.debug("Found {} sellers", sellers.getTotalElements());
        return sellers.map(s -> new SellerResponseDTO(
                s.getId(),
                s.getFirstName() + " " + s.getLastName(),
                s.getEmail(),
                s.isActive(),
                s.getCompanyName(),
                s.getAddress(),
                s.getCompanyContact(),
                s.getGst()
        ));
    }

    public String activateUser(Long userId) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        logger.info("Activating user with ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User with ID {} not found", userId);
            return new ResourceNotFoundException(messageSource.getMessage("user.not.registered", null, locale));
        });
        if (user.isActive()) {


            logger.warn("User with ID {} is already active", userId);
            throw new ForbiddenAccessException(messageSource.getMessage("user.already.active", null, locale));
            // return "User Already active";
        }
        user.setActive(true);
        userRepository.save(user);
        logger.info("User with ID {} activated successfully", userId);
        emailService.sendEmail(user.getEmail(), "account activated", "account activated successfully");
        return "Account activated";
    }

    public String deactivateUser(Long userId) throws MessagingException {
        logger.info("Deactivating user with ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
        {
            logger.error("User with ID {} not found", userId);
            return new ResourceNotFoundException("user not registered");
        });
        if (!user.isActive()) {
            logger.warn("User with ID {} is already deactivated", userId);
            throw new ForbiddenAccessException("user is already deactivated");
            //return "user is already deactivated";
        }
        user.setActive(false);
        userRepository.save(user);
        logger.info("User with ID {} deactivated successfully", userId);
        emailService.sendEmail(user.getEmail(), "account deactivated ", "account deactivated successfully");

        return "user successfully deactivated";
    }

    public String addMetaDataField(CategoryMetaDataField field) {
        Locale locale = LocaleContextHolder.getLocale();
        if (categoryMetaDataFieldRepository.findByNameIgnoreCase(field.getName()).isPresent()) {
            logger.warn("MetaDataField with name {} already exists", field.getName());
            throw new ResourceNotFoundException(messageSource.getMessage("field.name.already.exists", null, locale));


        }
        CategoryMetaDataField savedField = categoryMetaDataFieldRepository.save(field);
        return messageSource.getMessage("field.created.successfully", new Object[]{savedField.getId()}, locale);
    }

    public Page<CategoryMetaDataField> getAllMetaDataFields(Map<String, Object> filters, Pageable pageable) {
        //return all metadata fields if query is null or empty
        Specification<CategoryMetaDataField> spec = Specification.where(null);

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            spec = spec.and((root, query, cb) -> {
                if (field.equalsIgnoreCase("id")) {
                    try {
                        //where id=152
                        Long idValue = Long.parseLong(value.toString());
                        return cb.equal(root.get(field), idValue); // Exact match for id
                    } catch (NumberFormatException e) {
                        return cb.disjunction(); // Skip invalid id
                    }
                } else {
                    //where name like color
                    return cb.like(cb.lower(root.get(field).as(String.class)), "%" + value.toString().toLowerCase() + "%");
                }
            });
        }

        return categoryMetaDataFieldRepository.findAll(spec, pageable);
    }

    public String addCategory(CategoryRequestDTO dto) {

        Category parent = null;

        // If parentId is provided
        if (dto.getParentId() != null) {
            parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid parent category ID"));

            // Parent should not have products
            if (productRepository.existsByCategory_Id(dto.getParentId())) {
                throw new IllegalStateException("Parent category is already linked to a product");
            }

            // Check if name exists in siblings or ancestors
            if (isNamePresentInSiblingsOrAncestors(dto.getName(), parent,null)) {
                throw new IllegalArgumentException("Category name already exists among ancestors ,descendent and  siblings");
            }
        } else {
            // Root category check
            if (categoryRepository.existsByNameIgnoreCaseAndParentCategoryIsNull(dto.getName())) {
                throw new IllegalArgumentException("Category name already exists at root level");
            }
        }

        // Create and save new category
        Category newCategory = new Category();
        newCategory.setName(dto.getName());
        newCategory.setParentCategory(parent);

        Category saved = categoryRepository.save(newCategory);

        return "Category created successfully with ID: " + saved.getId();
    }
    // Check if name exists in siblings or ancestors or descendent
private boolean isNamePresentInSiblingsOrAncestors(String name, Category parent, Long currentCategoryId) {
    if (currentCategoryId != null) {
        if (categoryRepository.existsByNameIgnoreCaseAndParentCategoryAndIdNot(name, parent, currentCategoryId)) {
            return true;
        }
        Category current = parent;
        while (current != null) {
            if (current.getName().equalsIgnoreCase(name)) {
                return true;
            }
            current = current.getParentCategory();
        }
        return isNamePresentInDescendants(categoryRepository.findById(currentCategoryId).orElse(null), name);
    } else {
        if (categoryRepository.existsByNameIgnoreCaseAndParentCategory(name, parent)) {
            return true;
        }
        Category current = parent;
        while (current != null) {
            if (current.getName().equalsIgnoreCase(name)) {
                return true;
            }
            current = current.getParentCategory();
        }
        return false;
    }
}
    private boolean isNamePresentInDescendants(Category category, String name) {
        if (category == null) return false;

        List<Category> children = categoryRepository.findByParentCategory(category);
        for (Category child : children) {
            if (child.getName().equalsIgnoreCase(name)) {
                return true;
            }
            if (isNamePresentInDescendants(child, name)) {
                return true;
            }
        }
        return false;
    }

    public ViewCategoryResponse getOneCategory(Long categoryId){
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("no category with this id"));
        List<BasicCategory>parentCategories=getParentCategories(category);
        List<BasicCategory>childCategories=getChildCategories(category);
        List<MetadataFieldWithValues>metaFields=getMetaFields(category);
        return new ViewCategoryResponse(category.getId(),category.getName(),parentCategories,childCategories,metaFields);
    }
    private List<BasicCategory> getParentCategories(Category category) {
        // Logic to fetch parent categories
        List<BasicCategory>parentCategories=new ArrayList<>();
        //if no parent
        if(category.getParentCategory()==null){
           return parentCategories;
        }
        Category currentCategory=category.getParentCategory();
        while(currentCategory!=null)
        {
            parentCategories.add(new BasicCategory(currentCategory.getId(),currentCategory.getName()));
            currentCategory=currentCategory.getParentCategory();
        }
        return parentCategories;
    }
    private List<BasicCategory> getChildCategories(Category category) {
        // Logic to fetch child categories
        List<BasicCategory>childCategories=new ArrayList<>();
        List<Category>children=categoryRepository.findByParentCategory(category);
        for(Category child:children){
            childCategories.add(new BasicCategory(child.getId(),child.getName()));
        }
        return childCategories;
    }
    private List<MetadataFieldWithValues> getMetaFields(Category category) {
        // Logic to fetch metadata fields
        List<MetadataFieldWithValues>metadataFields=new ArrayList<>();
        List<CategoryMetaDataFieldValues>metaFieldValues=categoryMetaFieldValueRespository.findByCategory(category);
        for(CategoryMetaDataFieldValues metaFieldValue:metaFieldValues){
            metadataFields.add(new MetadataFieldWithValues(metaFieldValue.getCategoryMetaDataField().getName(),metaFieldValue.getFieldValues()));

        }
        return metadataFields;
    }
    public List<ViewCategoryResponse> getAllCategories(int offset,int max,String sortBy,String order,String query){
        Sort.Direction direction=order.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sortBy));

        Page<Category> categoryPage;

        if (query != null && !query.isBlank()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(query, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }

        List<ViewCategoryResponse> responses = new ArrayList<>();

        for (Category category : categoryPage.getContent()) {
            List<BasicCategory> parents = getParentCategories(category);
            List<BasicCategory> children = getChildCategories(category);
            List<MetadataFieldWithValues> metaFields = getMetaFields(category);
            ViewCategoryResponse response = new ViewCategoryResponse(
                    category.getId(),
                    category.getName(),
                    parents,
                    children,
                    metaFields
            );
            responses.add(response);
        }

        return responses;
    }
    public String updateCategory(UpdateCategoryRequestDTO dto)
    {//we want to update  the category
        Category categoryToUpdate=categoryRepository.findById(dto.getCategoryId()).orElseThrow(()->new ResourceNotFoundException("no category found"));
        if(isNamePresentInSiblingsOrAncestors(dto.getName(),categoryToUpdate.getParentCategory(),dto.getCategoryId())){
            throw new ForbiddenAccessException("category name already exist among ancestor or sibling");

        }
        categoryToUpdate.setName(dto.getName());
        categoryRepository.save(categoryToUpdate);
        return "Category updated successfully!!";
    }

}



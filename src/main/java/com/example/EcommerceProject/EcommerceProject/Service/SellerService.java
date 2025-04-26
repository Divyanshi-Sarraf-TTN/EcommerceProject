package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.*;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataFieldValues;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Address;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Role;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import com.example.EcommerceProject.EcommerceProject.Exception.MethodNotAllowedException;
import com.example.EcommerceProject.EcommerceProject.Exception.ResourceNotFoundException;
import com.example.EcommerceProject.EcommerceProject.Repository.*;
import com.example.EcommerceProject.EcommerceProject.Token.Token;
import com.example.EcommerceProject.EcommerceProject.Token.TokenRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SellerService {

    private static final Logger logger = LoggerFactory.getLogger(SellerService.class);

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMetaFieldValueRepository categoryMetaFieldValueRepository;

    public String registerSeller(SellerRequestDTO sellerRequestDto) throws MessagingException {
        logger.info("Registering seller with email: {}", sellerRequestDto.getEmail());

        String email = sellerRequestDto.getEmail();

        if (customerRepository.existsByEmail(email) || adminRepository.existsByEmail(email)) {
            logger.warn("Email already registered: {}", email);
            throw new MethodNotAllowedException("Email id already registered");
        }

        if (!sellerRequestDto.getConfirmPassword().equals(sellerRequestDto.getPassword())) {
            logger.warn("Password mismatch for email: {}", email);
            throw new MethodNotAllowedException("Password Doesn't Match");
        }

        if (sellerRepository.findByEmail(email).isPresent()) {
            logger.warn("Email already exists in seller table: {}", email);
            throw new MethodNotAllowedException("Email id already registered");
        }

        if (sellerRepository.findByGst(sellerRequestDto.getGst()).isPresent()) {
            logger.warn("Duplicate GST detected: {}", sellerRequestDto.getGst());
            throw new MethodNotAllowedException("Gst should be unique");
        }

        if (sellerRepository.findByCompanyName(sellerRequestDto.getCompanyName()).isPresent()) {
            logger.warn("Duplicate company name detected: {}", sellerRequestDto.getCompanyName());
            throw new MethodNotAllowedException("CompanyName should be unique");
        }

        Seller seller = new Seller();
        BeanUtils.copyProperties(sellerRequestDto, seller);

        Role role = roleRepository.findByAuthority("ROLE_SELLER")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        seller.setRole(role);
        seller.setPassword(passwordEncoder.encode(sellerRequestDto.getPassword()));
        seller.setActive(false);
        seller.setPasswordUpdateDate(LocalDate.from(LocalDateTime.now()));

        AddressRequestDTO addressRequestDTO = sellerRequestDto.getAddress();
        Address address = new Address();
        BeanUtils.copyProperties(addressRequestDTO, address);

        sellerRepository.save(seller);

        address.setSeller(seller);
        addressRepository.save(address);

        seller.setAddress(address);
        sellerRepository.save(seller);

        String token = UUID.randomUUID().toString();
        Date expiryTime = new Date(System.currentTimeMillis() + 3*60*60*1000);
        Token tokenEntity = new Token(seller.getEmail(), token, expiryTime);
        tokenRepository.save(tokenEntity);

        sendActivationEmail(seller.getEmail());

        logger.info("Seller registration successful: {}", seller.getEmail());
        return "Seller registered successfully! Please check your email.";
    }

    @Async
    public void sendActivationEmail(String email) throws MessagingException {
        logger.info("Sending activation email to: {}", email);
        String emailBody = "Account has been created! Waiting for approval";
        emailService.sendEmail(email, "Account created", emailBody);
    }

    public SellerProfileResponseDTO viewSellerProfile(String token) {
        logger.info("Viewing seller profile for token: {}", token);
        Token token1 = tokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("No token found"));
        Seller seller1 = sellerRepository.findByEmail(token1.getEmail()).orElseThrow(() -> new ResourceNotFoundException("No seller found"));

        SellerProfileResponseDTO sellerProfileResponseDTO = new SellerProfileResponseDTO();
        sellerProfileResponseDTO.setId(seller1.getId());
        sellerProfileResponseDTO.setFirstName(seller1.getFirstName());
        sellerProfileResponseDTO.setLastName(seller1.getLastName());
        sellerProfileResponseDTO.setActive(seller1.isActive());
        sellerProfileResponseDTO.setCompanyContact(seller1.getCompanyContact());
        sellerProfileResponseDTO.setCompanyName(seller1.getCompanyName());
        sellerProfileResponseDTO.setGst(seller1.getGst());
        if (seller1.getImage() != null) {
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/users/images/")
                    .path(seller1.getImage())
                    .toUriString();
            sellerProfileResponseDTO.setImage(imageUrl);
        }
        System.out.println(seller1.getImage());
        logger.info("Seller profile fetched successfully for email: {}", seller1.getEmail());
        return sellerProfileResponseDTO;
    }

    public String updateSellerProfile(String token, SellerRequestDTO sellerRequestDTO) {
        logger.info("Updating seller profile for token: {}", token);
        Token token1 = tokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("No token found"));
        Seller seller1 = sellerRepository.findByEmail(token1.getEmail()).orElseThrow(() -> new ResourceNotFoundException("No seller found with email"));

        if (sellerRequestDTO.getFirstName() != null && !sellerRequestDTO.getFirstName().isBlank()) {
            seller1.setFirstName(sellerRequestDTO.getFirstName());
        }

        List<String> listOfErrors = new ArrayList<>();

        if (sellerRepository.existsByCompanyContact(sellerRequestDTO.getCompanyContact())) listOfErrors.add("Company Contact already exists");
        if (sellerRepository.existsByGst(sellerRequestDTO.getGst())) listOfErrors.add("Gst already exists");
        if (sellerRepository.existsByCompanyName(sellerRequestDTO.getCompanyName())) listOfErrors.add("Company Name already exists");

        if (!listOfErrors.isEmpty()) {
            logger.warn("Validation errors during profile update: {}", listOfErrors);
            throw new MethodNotAllowedException("" + listOfErrors.toString());
        }

        if (sellerRequestDTO.getLastName() != null && !sellerRequestDTO.getLastName().isBlank()) {
            seller1.setLastName(sellerRequestDTO.getLastName());
        }

        if (sellerRequestDTO.getCompanyContact() != null) {
            seller1.setCompanyContact(sellerRequestDTO.getCompanyContact());
        }

        if (sellerRequestDTO.getCompanyName() != null && !sellerRequestDTO.getCompanyName().isBlank()) {
            seller1.setCompanyName(sellerRequestDTO.getCompanyName());
        }

        if (sellerRequestDTO.getGst() != null && !sellerRequestDTO.getGst().isBlank()) {
            seller1.setGst(sellerRequestDTO.getGst());
        }

        sellerRepository.save(seller1);
        logger.info("Seller profile updated successfully for email: {}", seller1.getEmail());
        return "Profile updated successfully";
    }
    public List<ViewLeafCategory> viewLeafCategory() {
        List<Category> categories = categoryRepository.findAll();
        List<ViewLeafCategory> response = new ArrayList<>();
        for (Category category : categories) {
            if (categoryRepository.findByParentCategory(category).isEmpty()) {
                ViewLeafCategory viewLeafCategory = new ViewLeafCategory();
                viewLeafCategory.setCategoryId(category.getId());
                viewLeafCategory.setCategoryName(category.getName());
                viewLeafCategory.setParentHierarchy(getParentCategories(category));
                viewLeafCategory.setMetadataFields(getMetaFields(category));
                response.add(viewLeafCategory);
            }
        }
        return response;
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
    private List<MetadataFieldWithValues> getMetaFields(Category category) {
        // Logic to fetch metadata fields
        List<MetadataFieldWithValues>metadataFields=new ArrayList<>();
        List<CategoryMetaDataFieldValues>metaFieldValues=categoryMetaFieldValueRepository.findByCategory(category);
        for(CategoryMetaDataFieldValues metaFieldValue:metaFieldValues){
            metadataFields.add(new MetadataFieldWithValues(metaFieldValue.getCategoryMetaDataField().getName(),metaFieldValue.getFieldValues()));

        }
        return metadataFields;
    }
}

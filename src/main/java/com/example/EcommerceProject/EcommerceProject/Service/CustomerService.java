package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.*;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.Category;
import com.example.EcommerceProject.EcommerceProject.Entity.Category.CategoryMetaDataFieldValues;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Address;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Role;
import com.example.EcommerceProject.EcommerceProject.Exception.ForbiddenAccessException;
import com.example.EcommerceProject.EcommerceProject.Exception.ResourceNotFoundException;
import com.example.EcommerceProject.EcommerceProject.Exception.UserNotFoundException;
import com.example.EcommerceProject.EcommerceProject.Repository.*;
import com.example.EcommerceProject.EcommerceProject.Token.Token;
import com.example.EcommerceProject.EcommerceProject.Token.TokenRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private     ProductRepository productRepository;
    @Autowired
    private VariationRepository variationRepository;
    

    @Transactional
    public String registerCustomer(CustomerRequestDTO request) throws MessagingException {
        logger.info("Registering customer with email: {}", request.getEmail());

        String email = request.getEmail();

        if (sellerRepository.existsByEmail(email) || adminRepository.existsByEmail(email)) {
            logger.warn("Email already registered in another role: {}", email);
            throw new ForbiddenAccessException("Email id already registered");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            logger.warn("Passwords do not match for email: {}", email);
            throw new ForbiddenAccessException("Passwords do not match");
        }

        if (customerRepository.findByEmail(email).isPresent()) {
            logger.warn("Customer already exists with email: {}", email);
            throw new ForbiddenAccessException("Email id already registered");
        }

        if (customerRepository.findByContact(request.getContact()).isPresent()) {
            logger.warn("Duplicate contact for email: {}", email);
            throw new ForbiddenAccessException("Contact Number is duplicate");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(email);
        customer.setContact(request.getContact());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setActive(false);
        customer.setPasswordUpdateDate(LocalDate.from(LocalDateTime.now()));
        customer.setLocked(false);
        customer.setInvalidAttemptCount(0);

        Role role = roleRepository.findByAuthority("ROLE_CUSTOMER")
                .orElseThrow(() -> {
                    logger.error("Customer role not found in DB");
                    return new ResourceNotFoundException("Role not found");
                });

        customer.setRole(role);
        logger.debug("Assigned role to customer: {}", role.getAuthority());

//        AddressRequestDTO addressRequestDTO = request.getAddress();
//
//        if (addressRequestDTO!=null) {
//            Address address = new Address();
//            address.setAddressLine(addressRequestDTO.getAddressLine());
//            address.setLabel(addressRequestDTO.getLabel());
//            address.setCity(addressRequestDTO.getCity());
//            address.setState(addressRequestDTO.getState());
//            address.setCountry(addressRequestDTO.getCountry());
//            address.setZipCode(addressRequestDTO.getZipCode());
//
//            address.getCustomers().add(customer);
//            customer.getAddresses().add(address);
//        }


//
//        List<Customer> customers = Optional.ofNullable(address.getCustomers()).orElse(new ArrayList<>());
//        customers.add(customer);
//        address.setCustomers(customers);

//        List<Address> addresses = Optional.ofNullable(customer.getAddresses()).orElse(new ArrayList<>());
//        addresses.add(address);
//        customer.setAddresses(addresses);

        customerRepository.save(customer);
        //addressRepository.save(address);

        String token = UUID.randomUUID().toString();
        Date expiryTime = new Date(System.currentTimeMillis() + 60 * 1000 * 60 * 3); // 3

        Token tokenEntity = new Token(customer.getEmail(), token, expiryTime);
        tokenRepository.save(tokenEntity);

        sendActivationEmail(customer.getEmail(), token);

        logger.info("Customer registered successfully: {}", email);
        return "Customer registered successfully! Please check your email to activate your account.";
    }


    public void sendActivationEmail(String email, String token) throws MessagingException {
        String activationLink = "http://localhost:8080/api/auth/customers/activate?token=" + token;
        logger.info("Sending activation email to: {}", email);
        String emailBody = "Click the link to activate your account: " + activationLink;
        emailService.sendEmail(email, "Activate Your Account", emailBody);
    }

    public String activateCustomer(String token) throws MessagingException {
        logger.info("Activating customer with token: {}", token);
        Token tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.error("Invalid activation token: {}", token);
                    return new ResourceNotFoundException("Invalid activation token");
                });

        if (new Date().after(tokenEntity.getExpiresAt())) {
            logger.warn("Token expired: {}", token);
            String newToken = UUID.randomUUID().toString();
            Date newExpiryTime = new Date(System.currentTimeMillis() + 1 * 60 * 1000); // 1 min

            Token newTokenEntity = new Token(tokenEntity.getEmail(), newToken, newExpiryTime);
            tokenRepository.save(newTokenEntity);
            sendActivationEmail(tokenEntity.getEmail(), newToken);
            tokenRepository.delete(tokenEntity);

            return "Activation token has expired. Please check your email for a new token.";
        }

        Customer customer = (Customer) customerRepository.findByEmail(tokenEntity.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found for token"));

        customer.setActive(true);
        customerRepository.save(customer);
        tokenRepository.delete(tokenEntity);

        logger.info("Customer account activated for email: {}", customer.getEmail());
        return "Account activated successfully!";
    }

    public String resendactivate(String email) throws MessagingException {
        logger.info("Resending activation email to: {}", email);
        Customer customer = (Customer) customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not registered"));

        if (customer.isActive()) {
            logger.warn("Customer already active: {}", email);
            throw new ForbiddenAccessException("Customer already active");
        }

        tokenRepository.deleteByEmail(email);

        String token = UUID.randomUUID().toString();
        Date expiryTime = new Date(System.currentTimeMillis() + 3 * 60 * 60 * 1000); // 3 hours
        Token tokenEntity = new Token(email, token, expiryTime);
        tokenRepository.save(tokenEntity);

        sendActivationEmail(email, token);
        return "New activation email sent!";
    }

    public CustomerProfileResponseDTO viewProfileOfCustomer(String token) throws MessagingException {
        logger.info("Viewing customer profile using token: {}", token);
        Token token1 = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        Customer customer = (Customer) customerRepository.findByEmail(token1.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        CustomerProfileResponseDTO dto = new CustomerProfileResponseDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setIsActive(customer.isActive());
        dto.setContact(customer.getContact());
        if (customer.getImage() != null) {
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/users/images/")
                    .path(customer.getImage())
                    .toUriString();
            dto.setImage(imageUrl);
        }

        logger.debug("Returning profile for customer ID: {}", customer.getId());
        return dto;
    }

    public List<AddressResponseDTO> viewAddressesOfCustomer(String token) throws MessagingException {
        logger.info("Fetching addresses for customer token: {}", token);
        Token token1 = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        Customer customer = (Customer) customerRepository.findByEmail(token1.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return customer.getAddresses().stream()
                .filter(addr -> !addr.getIsDeleted())
                .map(addr -> {
                    AddressResponseDTO dto = new AddressResponseDTO();
                    dto.setId(addr.getId());
                    dto.setAddressLine(addr.getAddressLine());
                    dto.setLabel(addr.getLabel());
                    dto.setCity(addr.getCity());
                    dto.setState(addr.getState());
                    dto.setCountry(addr.getCountry());
                    dto.setZipCode(addr.getZipCode());
                    return dto;
                }).toList();
    }

    public String updateCustomerProfile(String token, CustomerRequestDTO customerRequestDTO) throws MessagingException {
        logger.info("Updating customer profile using token: {}", token);
        Token token1 = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        Customer customer = (Customer) customerRepository.findByEmail(token1.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customerRequestDTO.getFirstName() != null && !customerRequestDTO.getFirstName().isBlank()) {
            customer.setFirstName(customerRequestDTO.getFirstName());
        }
        if (customerRequestDTO.getLastName() != null && !customerRequestDTO.getLastName().isBlank()) {
            customer.setLastName(customerRequestDTO.getLastName());
        }
        if (customerRequestDTO.getContact() != null) {
            customer.setContact(customerRequestDTO.getContact());
        }

        customerRepository.save(customer);
        logger.info("Customer profile updated for email: {}", customer.getEmail());
        emailService.sendEmail(customer.getEmail(), "Profile update", "Profile updated successfully");

        return "Customer profile updated successfully";
    }

    @Transactional
    public String addAddress(String token, AddressRequestDTO addressRequestDTO) {
        logger.info("Adding address for customer token: {}", token);
        Token token1 = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        Customer customer = (Customer) customerRepository.findByEmail(token1.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Address address = new Address();
        address.setAddressLine(addressRequestDTO.getAddressLine());
        address.setLabel(addressRequestDTO.getLabel());
        address.setCity(addressRequestDTO.getCity());
        address.setState(addressRequestDTO.getState());
        address.setCountry(addressRequestDTO.getCountry());
        address.setZipCode(addressRequestDTO.getZipCode());

        addressRepository.save(address);
        customer.addAddress(address);

        customerRepository.save(customer);
        logger.info("Address added for customer ID: {}", customer.getId());

        return "New address added successfully";
    }

    public String deleteAddress(String token, Long id) {
        logger.info("Deleting address ID: {} using token: {}", id, token);
        Token token1 = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        Customer customer = (Customer) customerRepository.findByEmail(token1.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!customer.getAddresses().contains(address)) {
            logger.warn("Address ID {} not linked to customer ID: {}", id, customer.getId());
            throw new ForbiddenAccessException("No address linked to this customer");
        }

        address.setIsDeleted(true);
        addressRepository.save(address);
        logger.info("Address marked as deleted for customer ID: {}", customer.getId());

        return "Address deleted successfully";
    }
    public List<SameLevelCategory> getSameLevelCategories(Long categoryId) {
        List<Category> categories;

        // Case 1: If no ID is passed, return all root-level categories
        if (categoryId == null) {
            categories = categoryRepository.findByParentCategoryIsNull();
        }
        // Case 2: If ID is passed, return all immediate children
        else {
            Category parentCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found"));
            categories = categoryRepository.findByParentCategory(parentCategory);
        }

        // Convert entities to DTOs
        List<SameLevelCategory> response = new ArrayList<>();
        for (Category category : categories) {
            SameLevelCategory dto = new SameLevelCategory();
            dto.setId(category.getId());
            dto.setName(category.getName());

            // Set parentId only if it exists
            if (category.getParentCategory() != null) {
                dto.setParentId(category.getParentCategory().getId());
            }

            response.add(dto);
        }

        return response;
    }


}

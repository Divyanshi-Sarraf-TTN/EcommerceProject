package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.DTO.CustomerRequestDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.CustomerResponseDTO;
import com.example.EcommerceProject.EcommerceProject.DTO.SellerResponseDTO;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Seller;
import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import com.example.EcommerceProject.EcommerceProject.Repository.CustomerRepository;
import com.example.EcommerceProject.EcommerceProject.Repository.SellerRepository;
import com.example.EcommerceProject.EcommerceProject.Repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public Page<SellerResponseDTO>getAllSellers(String emailFilter,Pageable pageable )
    {
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
        logger.info("Activating user with ID: {}", userId);
        User user=userRepository.findById(userId).orElseThrow(()->{
            logger.error("User with ID {} not found", userId);
           return new RuntimeException("user not registered");
        });
        if(user.isActive())
        {
            logger.warn("User with ID {} is already active", userId);
            return "User Already active";
        }
        user.setActive(true);
        userRepository.save(user);
        logger.info("User with ID {} activated successfully", userId);
        emailService.sendEmail(user.getEmail(),"account activated","account activated successfully");
        return "Account activated";
    }
    public String deactivateUser(Long userId) throws MessagingException {
        logger.info("Deactivating user with ID: {}", userId);
       User user=userRepository.findById(userId).orElseThrow(()->
       {
           logger.error("User with ID {} not found", userId);
           return new RuntimeException("user not registered");
       });
       if(!user.isActive())
       {
           logger.warn("User with ID {} is already deactivated", userId);
           return "user is already deactivated";
       }
       user.setActive(false);
       userRepository.save(user);
        logger.info("User with ID {} deactivated successfully", userId);
       emailService.sendEmail(user.getEmail(),"account deactivated ","account deactivated successfully");

       return "user successfully deactivated";
    }

}
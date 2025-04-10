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

@Service
public class AdminService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    public Page<CustomerResponseDTO> getAllCustomers(String emailFilter, Pageable pageable) {
        Page<Customer> customers = customerRepository.findByEmailContainingIgnoreCase(emailFilter, pageable);
        return customers.map(c -> new CustomerResponseDTO(
                c.getId(),
                c.getFirstName() + " " + c.getLastName(),
                c.getEmail(),
                c.isActive()
        ));
    }
    public Page<SellerResponseDTO>getAllSellers(String emailFilter,Pageable pageable )
    {
        Page<Seller> sellers = sellerRepository.findByEmailContainingIgnoreCase(emailFilter, pageable);
        return sellers.map(s -> new SellerResponseDTO(
                s.getId(),
                s.getFirstName() + " " + s.getLastName(),
                s.getEmail(),
                s.isActive(),
                s.getCompanyName(),
                s.getAddress(),
                s.getCompanyContact()
        ));
    }

    public String activateUser(Long userId) throws MessagingException {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("user not registered"));
        if(user.isActive())
        {
            return "User Already active";
        }
        user.setActive(true);
        userRepository.save(user);
        emailService.sendEmail(user.getEmail(),"account activated","account activated successfully");
        return "Account activated";
    }
    public String deactivateUser(Long userId) throws MessagingException {
       User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("user not registered"));
       if(!user.isActive())
       {
           return "user is already deactivated";
       }
       user.setActive(false);
       userRepository.save(user);
       emailService.sendEmail(user.getEmail(),"account deactivated ","account deactivated successfully");

       return "user successfully deactivated";
    }

}
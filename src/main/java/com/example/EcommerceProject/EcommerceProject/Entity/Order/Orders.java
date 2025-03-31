package com.example.EcommerceProject.EcommerceProject.Entity.Order;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity

public class Orders {
    @Id
    private Long id;
    @NotNull(message = "Amount Paid is required")
    @DecimalMin(value = "0.1", inclusive = true, message = "Amount Paid must be greater than 0")
    private Double amountPaid;

    @NotNull(message = "Date Created is required")
    @PastOrPresent(message = "Date Created must be a past or present date")
    private LocalDate dateCreated;

    @NotBlank(message = "Payment Method is required")
    @Size(min = 3, max = 50, message = "Payment Method must be between 3 and 50 characters")
    private String paymentMethod;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String customerAddressCity;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    private String customerAddressState;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    private String customerAddressCountry;

    @NotBlank(message = "Address Line is required")
    @Size(min = 5, max = 255, message = "Address Line must be between 5 and 255 characters")
    private String customerAddressAddressLine;

    @NotNull(message = "Zip Code is required")
    @Digits(integer = 6, fraction = 0, message = "Zip Code must be exactly 6 digits")
    private Double customerAddressZipCode;

    @NotBlank(message = "Label is required")
    @Size(min = 3, max = 20, message = "Label must be between 3 and 20 characters (e.g., Home, Work)")
    private String customerAddressLabel;

    @ManyToOne
    @JoinColumn(name="customerUserId")
    private Customer customer;
    //one to many mapping with order and order product
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    List<OrderProduct>orderProducts;
}

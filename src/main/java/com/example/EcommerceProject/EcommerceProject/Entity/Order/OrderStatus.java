package com.example.EcommerceProject.EcommerceProject.Entity.Order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class OrderStatus {
    @Id
    private Long id;
    @NotBlank(message = "From Status is required")
    @Size(min = 2, max = 50, message = "From Status must be between 2 and 50 characters")
    private String fromStatus;

    @NotBlank(message = "To Status is required")
    @Size(min = 2, max = 50, message = "To Status must be between 2 and 50 characters")
    private String toStatus;

    @NotBlank(message = "Transition Notes/Comments are required")
    @Size(min = 10, max = 1000, message = "Transition Notes/Comments must be between 10 and 1000 characters")
    private String transitionNotesComments;

    @NotBlank(message = "Transition Date is required")

    private String transitionDate;

    @ManyToOne
    @JoinColumn(name="orderProductId")
    private OrderProduct orderProduct;
}

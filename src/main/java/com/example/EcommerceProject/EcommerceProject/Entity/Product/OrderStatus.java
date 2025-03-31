package com.example.EcommerceProject.EcommerceProject.Entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class OrderStatus {
    @Id
    private Integer id;
    private String fromStatus;
    private String toStatus;
    private String transitionNotesComments;
    private String transitionDate;
    @OneToOne
    @JoinColumn(name="orderProductId")
    private OrderProduct orderProduct;
}

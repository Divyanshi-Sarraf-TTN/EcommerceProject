package com.example.EcommerceProject.EcommerceProject.Entity.Cart;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Embeddable
public class CartID implements Serializable {
    private Long customerUserId;
    private Long productVariationId;

}

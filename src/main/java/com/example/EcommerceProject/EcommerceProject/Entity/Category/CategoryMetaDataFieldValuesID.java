package com.example.EcommerceProject.EcommerceProject.Entity.Category;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Embeddable
public class CategoryMetaDataFieldValuesID implements Serializable {
    private Long categoryId;
    private Long categoryMetaFieldId;


}

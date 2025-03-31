package com.example.EcommerceProject.EcommerceProject.Entity.Category;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CategoryMetaDataField {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;
    @OneToMany(mappedBy = "categoryMetaDataField",cascade = CascadeType.ALL)
    private List<CategoryMetaDataFieldValues> categoryMetaDataFieldValues;
}

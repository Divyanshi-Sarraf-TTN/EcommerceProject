package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Setter









@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ViewLeafCategory {
    private Long categoryId;
    private String categoryName;
    private List<BasicCategory> parentHierarchy;
    private List<MetadataFieldWithValues> metadataFields;
}

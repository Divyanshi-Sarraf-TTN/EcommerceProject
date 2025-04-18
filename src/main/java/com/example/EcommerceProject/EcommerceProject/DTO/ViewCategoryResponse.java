package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewCategoryResponse {
    private long id;
    private String categoryName;
    private List<BasicCategory> parentHierarchy;
    private List<BasicCategory> children;
    private List<MetadataFieldWithValues>metadataFields;
}

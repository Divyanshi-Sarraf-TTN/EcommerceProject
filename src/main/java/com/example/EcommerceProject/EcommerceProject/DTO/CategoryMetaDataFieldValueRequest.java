package com.example.EcommerceProject.EcommerceProject.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CategoryMetaDataFieldValueRequest {
    private Long categoryId;
    private List <FieldValuePair> fieldValuePairs;
    @Getter
    @Setter
    public static  class FieldValuePair{
        private Long metadataFieldId;
        private List<String> values;
    }
}

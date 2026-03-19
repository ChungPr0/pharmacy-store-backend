package com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductDetailResponse {
    private Long id;
    private String name;
    private String slug;
    private BigDecimal price;
    private String description;
    private Boolean isActive;
    private Long categoryId;
    private String categoryName;
    private List<String> images;
    private List<ProductAttributeResponse> attributes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductAttributeResponse {
        private String name;
        private String value;
    }
}

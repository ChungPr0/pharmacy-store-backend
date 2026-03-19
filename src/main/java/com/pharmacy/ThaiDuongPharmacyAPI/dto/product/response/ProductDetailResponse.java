package com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String slug;
    private BigDecimal price;
    private Long totalStockQuantity;
    private String categoryName;
    private String categorySlug;
    private List<String> images;
    private List<ProductAttribute> attributes;
    private String description;
    private Boolean isBestSeller;
}

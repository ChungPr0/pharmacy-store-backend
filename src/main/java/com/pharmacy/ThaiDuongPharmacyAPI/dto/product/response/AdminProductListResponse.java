package com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductListResponse {
    private Long id;
    private String name;
    private String slug;
    private String categoryName;
    private BigDecimal price;
    private Boolean isActive;
    private Long totalStockQuantity;
}

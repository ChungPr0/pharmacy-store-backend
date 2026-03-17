package com.pharmacy.ThaiDuongPharmacyAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponse {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
    private BigDecimal price;
    private Boolean isBestSeller;
    private Long totalStockQuantity;
}

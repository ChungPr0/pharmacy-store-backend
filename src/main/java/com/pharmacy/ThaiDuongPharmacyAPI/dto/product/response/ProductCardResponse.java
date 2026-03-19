package com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCardResponse {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
    private BigDecimal price;
    private String categoryName;
    private Boolean isBestSeller;
}

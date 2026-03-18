package com.pharmacy.ThaiDuongPharmacyAPI.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private Long productId;
    private String name;
    private String slug;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Double itemTotal;
}

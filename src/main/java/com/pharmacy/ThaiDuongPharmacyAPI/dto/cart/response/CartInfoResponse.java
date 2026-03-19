package com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartInfoResponse {
    private Long cartId;
    private Integer totalItems;
    private Double totalPrice;
    private List<CartItemResponse> items;
}

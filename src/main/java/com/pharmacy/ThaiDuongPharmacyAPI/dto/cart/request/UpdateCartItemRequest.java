package com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "ID sản phẩm không được để trống")
    private Long productId;

    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity;
}

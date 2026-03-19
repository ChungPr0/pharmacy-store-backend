package com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal price;

    public static OrderItemResponse fromEntity(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImageUrl(item.getProduct().getImageUrl())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
}

package com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderHistoryResponse {
    private Long id;
    private String orderCode;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;

    public static OrderHistoryResponse fromEntity(Order order) {
        return OrderHistoryResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}

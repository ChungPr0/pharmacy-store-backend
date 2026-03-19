package com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderAdminResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String orderCode;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private String receiverName;
    private String phoneNumber;

    public static OrderAdminResponse fromEntity(Order order) {
        return OrderAdminResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getFullName() : null)
                .orderCode(order.getOrderCode())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .receiverName(order.getReceiverName())
                .phoneNumber(order.getPhoneNumber())
                .build();
    }
}

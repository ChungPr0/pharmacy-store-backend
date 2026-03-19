package com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderDetailResponse {
    private Long id;
    private String orderCode;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    
    private String shippingAddressText;
    private String receiverName;
    private String phoneNumber;
    private String paymentMethod;
    private List<OrderItemResponse> items;

    public static OrderDetailResponse fromEntity(Order order) {
        return OrderDetailResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .shippingAddressText(order.getShippingAddressText())
                .receiverName(order.getReceiverName())
                .phoneNumber(order.getPhoneNumber())
                .paymentMethod(order.getPaymentMethod())
                .items(order.getItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}

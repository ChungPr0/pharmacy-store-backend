package com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private Order.OrderStatus status;
}

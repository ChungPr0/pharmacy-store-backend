package com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request;

import lombok.Data;

@Data
public class CancelOrderRequest {
    private String cancelReason;
}

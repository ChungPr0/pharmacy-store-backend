package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.CheckoutRequestDTO;

public interface OrderService {
    void checkout(CheckoutRequestDTO request);
}

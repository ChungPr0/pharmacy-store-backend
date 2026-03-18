package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.CheckoutRequestDTO;

public interface OrderService {
    void checkout(CheckoutRequestDTO request);
}

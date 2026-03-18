package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.CartItemRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.UpdateCartItemRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.CartInfoResponse;

public interface CartService {
    CartInfoResponse getCartInfo();
    void addProductToCart(CartItemRequestDTO request);
    void updateCartItemQuantity(UpdateCartItemRequestDTO request);
    void removeProductFromCart(Long productId);
}

package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.request.CartItemRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.request.UpdateCartItemRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.response.CartInfoResponse;

public interface CartService {
    CartInfoResponse getCartInfo();
    void addProductToCart(CartItemRequest request);
    void updateCartItemQuantity(UpdateCartItemRequest request);
    void removeProductFromCart(Long productId);
}

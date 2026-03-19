package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.request.CartItemRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.request.UpdateCartItemRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.response.CartInfoResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Cart APIs", description = "Các API quản lý Giỏ hàng của Khách hàng")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Lấy thông tin giỏ hàng", description = "Lấy toàn bộ sản phẩm, tổng số lượng và tổng tiền trong giỏ hàng hiện tại của khách hàng.")
    @GetMapping
    public ResponseEntity<ApiResponse<CartInfoResponse>> getCartInfo() {
        return ApiResponse.success("Lấy thông tin giỏ hàng thành công", cartService.getCartInfo());
    }

    @Operation(summary = "Thêm sản phẩm vào giỏ hàng", description = "Thêm một sản phẩm mới hoặc tăng số lượng nếu sản phẩm đã có trong giỏ.")
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Object>> addProductToCart(
            @Valid @RequestBody CartItemRequestDTO request) {
        cartService.addProductToCart(request);
        return ApiResponse.success("Đã thêm sản phẩm vào giỏ hàng");
    }

    @Operation(summary = "Cập nhật số lượng sản phẩm", description = "Cập nhật chính xác số lượng của một sản phẩm trong giỏ. Nếu số lượng <= 0, sản phẩm sẽ bị xóa khỏi giỏ.")
    @PutMapping("/items")
    public ResponseEntity<ApiResponse<Object>> updateCartItemQuantity(
            @Valid @RequestBody UpdateCartItemRequestDTO request) {
        cartService.updateCartItemQuantity(request);
        return ApiResponse.success("Cập nhật số lượng thành công");
    }

    @Operation(summary = "Xóa sản phẩm khỏi giỏ hàng", description = "Xóa hoàn toàn một sản phẩm khỏi giỏ hàng của khách hàng.")
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Object>> removeProductFromCart(
            @PathVariable Long productId) {
        cartService.removeProductFromCart(productId);
        return ApiResponse.success("Đã xóa sản phẩm khỏi giỏ hàng");
    }
}

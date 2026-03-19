package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.CheckoutRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Order APIs", description = "Các API quản lý Đơn hàng và Thanh toán")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Thanh toán (Checkout)", description = "Tạo đơn hàng từ các sản phẩm được chọn trong giỏ hàng, cập nhật tồn kho và làm sạch giỏ.")
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Object>> checkout(
            @Valid @RequestBody CheckoutRequestDTO request) {
        orderService.checkout(request);
        return ApiResponse.success("Đặt hàng thành công!");
    }
}

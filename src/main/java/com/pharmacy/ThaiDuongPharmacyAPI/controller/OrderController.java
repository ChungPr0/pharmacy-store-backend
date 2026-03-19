package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.CheckoutRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderDetailResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderHistoryResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Customer Order APIs", description = "Các API quản lý Đơn hàng và Thanh toán dành cho Khách hàng")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Lấy danh sách đơn hàng của tôi", description = "Lấy danh sách lịch sử đơn hàng của khách hàng hiện tại (Sắp xếp mới nhất lên đầu).")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<OrderHistoryResponse>>> getMyOrders() {
        return ApiResponse.success("Lấy danh sách đơn hàng thành công", orderService.getMyOrders());
    }

    @Operation(summary = "Xem chi tiết đơn hàng của tôi", description = "Lấy thông tin chi tiết của 1 đơn hàng cụ thể theo mã đơn hàng (orderCode).")
    @GetMapping("/me/{orderCode}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getMyOrderDetail(@PathVariable String orderCode) {
        return ApiResponse.success("Lấy chi tiết đơn hàng thành công", orderService.getMyOrderDetail(orderCode));
    }

    @Operation(summary = "Thanh toán (Checkout)", description = "Tạo đơn hàng từ các sản phẩm được chọn trong giỏ hàng, cập nhật tồn kho và làm sạch giỏ.")
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Object>> checkout(
            @Valid @RequestBody CheckoutRequest request) {
        orderService.checkout(request);
        return ApiResponse.success("Đặt hàng thành công!");
    }
}

package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.OrderSearchRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.OrderStatusUpdateRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderAdminResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderDetailResponse;
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
@RequestMapping("/api/v1/admin/orders")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Admin Order APIs", description = "Các API quản lý Đơn hàng dành cho Quản trị viên (Admin)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "Tìm kiếm và lọc đơn hàng", description = "Admin có thể tìm kiếm đơn hàng theo mã, tên, SĐT, hoặc lọc theo trạng thái, ngày đặt hàng.")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<OrderAdminResponse>>> searchOrders(
            @Valid @RequestBody OrderSearchRequest request) {
        return ApiResponse.success("Lấy danh sách đơn hàng thành công", orderService.searchOrders(request));
    }

    @Operation(summary = "Xem chi tiết đơn hàng (Admin)", description = "Admin xem toàn bộ chi tiết của 1 đơn hàng cụ thể bằng ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getAdminOrderDetail(@PathVariable Long id) {
        return ApiResponse.success("Lấy chi tiết đơn hàng thành công", orderService.getAdminOrderDetail(id));
    }

    @Operation(summary = "Cập nhật trạng thái đơn hàng", description = "Admin thay đổi trạng thái đơn hàng (PENDING, PAID, PROCESSING, SHIPPING, DELIVERED, CANCELLED).")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        orderService.updateOrderStatus(id, request);
        return ApiResponse.success("Cập nhật trạng thái đơn hàng thành công");
    }
}

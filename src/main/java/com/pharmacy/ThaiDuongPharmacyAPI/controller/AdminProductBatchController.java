package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request.ProductBatchImportRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.ProductBatchHistoryResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AdminProductBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/product-batches")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Admin Product Batch APIs", description = "Các API dành cho Admin quản lý lô hàng sản phẩm")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminProductBatchController {

    private final AdminProductBatchService adminProductBatchService;

    @Operation(summary = "Nhập hàng (Bulk Import)", description = "Nhập nhiều lô hàng cùng lúc, với ghi chú chung cho lần nhập.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/import")
    public ResponseEntity<ApiResponse<Object>> importProductBatches(
            @Valid @RequestBody ProductBatchImportRequest request) {
        adminProductBatchService.importProductBatches(request);
        return ApiResponse.success("Nhập hàng thành công");
    }

    @Operation(summary = "Lịch sử nhập hàng", description = "Lấy danh sách các lô hàng đã nhập, có phân trang và có thể lọc theo productId.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductBatchHistoryResponse>>> getProductBatches(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "15", required = false) int pageSize,
            @RequestParam(value = "productId", required = false) Long productId
    ) {
        return ApiResponse.success("Lấy lịch sử nhập hàng thành công", adminProductBatchService.getProductBatches(pageNo, pageSize, productId));
    }
}

package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.AdminProductRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.AdminProductDetailResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.AdminProductListResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.PagedResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/products")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Admin Product APIs", description = "Các API dành cho Admin quản lý sản phẩm")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @Operation(summary = "Lấy danh sách sản phẩm", description = "Lấy danh sách sản phẩm có phân trang, có thể lọc theo keyword và categorySlug. Bao gồm tổng số lượng tồn kho.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AdminProductListResponse>>> getProducts(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categorySlug", required = false) String categorySlug
    ) {
        PagedResponse<AdminProductListResponse> responseData = adminProductService.getProductList(pageNo, pageSize, keyword, categorySlug);
        ApiResponse<PagedResponse<AdminProductListResponse>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy danh sách sản phẩm thành công",
                responseData
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tạo sản phẩm mới", description = "Tạo sản phẩm mới bao gồm các thuộc tính và hình ảnh. Trả về id và slug.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createProduct(
            @Valid @RequestBody AdminProductRequestDTO request) {
        Map<String, Object> responseData = adminProductService.createProduct(request);
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Tạo sản phẩm thành công",
                responseData
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lấy chi tiết sản phẩm", description = "Lấy thông tin chi tiết của một sản phẩm dựa trên slug, bao gồm cả hình ảnh và thuộc tính.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<AdminProductDetailResponse>> getProductDetail(
            @PathVariable String slug) {
        AdminProductDetailResponse responseData = adminProductService.getProductDetail(slug);
        ApiResponse<AdminProductDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy chi tiết sản phẩm thành công",
                responseData
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cập nhật sản phẩm", description = "Cập nhật thông tin chi tiết của một sản phẩm dựa trên slug. Trả về slug mới (nếu có thay đổi).")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{slug}")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateProduct(
            @PathVariable String slug,
            @Valid @RequestBody AdminProductRequestDTO request) {
        Map<String, String> responseData = adminProductService.updateProduct(slug, request);
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Cập nhật sản phẩm thành công",
                responseData
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cập nhật trạng thái sản phẩm", description = "Bật/tắt trạng thái (is_active) của sản phẩm dựa trên slug.")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{slug}/toggle-status")
    public ResponseEntity<ApiResponse<Object>> toggleProductStatus(
            @PathVariable String slug) {
        adminProductService.toggleProductStatus(slug);
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Cập nhật trạng thái sản phẩm thành công",
                null
        );
        return ResponseEntity.ok(response);
    }
}

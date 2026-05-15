package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.review.request.ReviewRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.review.response.ReviewResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Product Review APIs", description = "Các API liên quan đến đánh giá sản phẩm")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @Operation(summary = "Lấy danh sách đánh giá của sản phẩm", description = "Trả về danh sách các đánh giá đã được duyệt của một sản phẩm.")
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success("Lấy danh sách đánh giá thành công", 
                productReviewService.getReviewsByProductId(productId, page, size));
    }

    @Operation(summary = "Tạo đánh giá mới", description = "Khách hàng đăng nhập để gửi đánh giá sản phẩm.")
    @PostMapping("/reviews")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<java.util.Map<String, Long>>> createReview(
            @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.created("Gửi đánh giá thành công", 
                productReviewService.createReview(request));
    }
}

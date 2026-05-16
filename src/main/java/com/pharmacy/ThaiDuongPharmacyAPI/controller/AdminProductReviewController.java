package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.review.request.AdminReviewReplyRequest;
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
@RequestMapping("/api/v1/admin/reviews")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Admin Product Review APIs", description = "Các API quản lý đánh giá sản phẩm dành cho Admin")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminProductReviewController {

    private final ProductReviewService productReviewService;

    @Operation(summary = "Admin trả lời bình luận", description = "Cập nhật câu trả lời của quản trị viên cho một bình luận.")
    @PatchMapping("/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> replyToReview(
            @PathVariable Long id,
            @Valid @RequestBody AdminReviewReplyRequest request) {
        
        productReviewService.replyToReview(id, request);
        return ApiResponse.success("Trả lời bình luận thành công");
    }

    @Operation(summary = "Xóa bình luận", description = "Admin có quyền xóa bình luận vi phạm.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        productReviewService.deleteReview(id);
        return ApiResponse.success("Xóa bình luận thành công");
    }
}

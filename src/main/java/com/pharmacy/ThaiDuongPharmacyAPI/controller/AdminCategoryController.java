package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.request.CategoryRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.CategoryTreeResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CategoryService;
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
@RequestMapping("/api/v1/admin/categories")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Admin Category APIs", description = "Các API dành cho Admin quản lý danh mục")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Lấy toàn bộ cây danh mục", description = "Trả về cấu trúc cây danh mục cho Admin (giống API public).")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getEntireCategoryTree() {
        return ApiResponse.success("Lấy toàn bộ cây danh mục thành công", categoryService.getCategoryTree());
    }

    @Operation(summary = "Tạo danh mục mới", description = "Tạo danh mục gốc hoặc danh mục con. Slug sẽ được tự động tạo từ tên.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryTreeResponse>> createCategory(
            @Valid @RequestBody CategoryRequestDTO request) {
        return ApiResponse.created("Tạo danh mục thành công", categoryService.createCategory(request));
    }

    @Operation(summary = "Cập nhật danh mục", description = "Cập nhật tên hoặc danh mục cha của một danh mục dựa trên slug.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{slug}")
    public ResponseEntity<ApiResponse<CategoryTreeResponse>> updateCategory(
            @PathVariable String slug,
            @Valid @RequestBody CategoryRequestDTO request) {
        return ApiResponse.success("Cập nhật danh mục thành công", categoryService.updateCategory(slug, request));
    }

    @Operation(summary = "Xóa danh mục", description = "Xóa danh mục dựa trên slug. Không cho phép xóa nếu danh mục đang chứa sản phẩm hoặc có danh mục con.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{slug}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable String slug) {
        categoryService.deleteCategory(slug);
        return ApiResponse.success("Xóa danh mục thành công");
    }
}

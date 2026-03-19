package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.CategoryHierarchyResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.CategoryTreeResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Category APIs", description = "Các API liên quan đến thao tác cấu trúc Danh mục")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Lấy Cấu trúc Cây Danh mục", description = "Trả về toàn bộ danh mục dưới dạng cây (gồm parent và các children lồng nhau).")
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getCategoryTree() {
        return ApiResponse.success("Lấy cây danh mục thành công", categoryService.getCategoryTree());
    }

    @Operation(summary = "Lấy Chi tiết Cấu trúc Danh mục", description = "Lấy thông tin danh mục bằng Slug cùng với danh sách các danh mục con trực tiếp của nó.")
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CategoryHierarchyResponse>> getCategoryHierarchy(@PathVariable String slug) {
        return ApiResponse.success("Lấy danh mục thành công", categoryService.getCategoryHierarchy(slug));
    }
}

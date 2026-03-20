package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request.ProductSearchRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.*;
import com.pharmacy.ThaiDuongPharmacyAPI.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Product APIs", description = "Các API liên quan đến thao tác dữ liệu Sản Phẩm")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Lấy danh sách Sản phẩm Bán chạy", description = "Trả về danh sách các sản phẩm đang được đánh dấu là bán chạy, có tồn kho và hạn sử dụng an toàn.")
    @GetMapping("/best-sellers")
    public ResponseEntity<ApiResponse<PageResponse<ProductCardResponse>>> getBestSellers(
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success("Lấy danh sách sản phẩm thành công", productService.getValidBestSellers(limit));
    }

    @Operation(summary = "Lấy danh sách Sản phẩm Mới nhất", description = "Trả về danh sách các sản phẩm mới thêm vào hệ thống gần đây nhất.")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<PageResponse<ProductCardResponse>>> getLatestProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success("Lấy danh sách sản phẩm thành công", productService.getValidLatestProducts(limit));
    }

    @Operation(summary = "Tìm kiếm & Lọc Sản phẩm", description = "API đa năng để lọc theo danh mục, tìm kiếm theo từ khoá, phân trang và sắp xếp.")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ProductSearchResponse>>> searchProducts(
            @Valid @RequestBody ProductSearchRequest request) {
        return ApiResponse.success("Tìm kiếm sản phẩm thành công", productService.searchProducts(request));
    }

    @Operation(summary = "Lấy Chi tiết một Sản phẩm", description = "Trả về toàn bộ thông tin chi tiết của một sản phẩm bao gồm ảnh, thuộc tính và tồn kho dựa trên Slug.")
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(@PathVariable String slug) {
        return ApiResponse.success("Lấy chi tiết sản phẩm thành công", productService.getProductDetail(slug));
    }

    @Operation(summary = "Lấy danh sách Sản phẩm Liên quan", description = "Lấy danh sách các sản phẩm cùng danh mục để gợi ý cho người dùng.")
    @GetMapping("/{slug}/related")
    public ResponseEntity<ApiResponse<List<RelatedProductResponse>>> getRelatedProducts(
            @PathVariable String slug,
            @RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success("Lấy sản phẩm liên quan thành công", productService.getRelatedProducts(slug, limit));
    }
}

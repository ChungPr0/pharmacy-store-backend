package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.ProductSearchRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.*;
import com.pharmacy.ThaiDuongPharmacyAPI.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<PagedResponse<ProductCardResponse>>> getBestSellers(
            @RequestParam(defaultValue = "10") int limit) {
        PagedResponse<ProductCardResponse> products = productService.getValidBestSellers(limit);
        ApiResponse<PagedResponse<ProductCardResponse>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy danh sách sản phẩm thành công",
                products
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy danh sách Sản phẩm Mới nhất", description = "Trả về danh sách các sản phẩm mới thêm vào hệ thống gần đây nhất.")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<PagedResponse<ProductCardResponse>>> getLatestProducts(
            @RequestParam(defaultValue = "10") int limit) {
        PagedResponse<ProductCardResponse> products = productService.getValidLatestProducts(limit);
        ApiResponse<PagedResponse<ProductCardResponse>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy danh sách sản phẩm thành công",
                products
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tìm kiếm & Lọc Sản phẩm", description = "API đa năng để lọc theo danh mục, tìm kiếm theo từ khoá, phân trang và sắp xếp.")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<ProductSearchResponse>>> searchProducts(
            @Valid @RequestBody ProductSearchRequestDTO request) {
        PagedResponse<ProductSearchResponse> products = productService.searchProducts(request);
        ApiResponse<PagedResponse<ProductSearchResponse>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Tìm kiếm sản phẩm thành công",
                products
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy Chi tiết một Sản phẩm", description = "Trả về toàn bộ thông tin chi tiết của một sản phẩm bao gồm ảnh, thuộc tính và tồn kho dựa trên Slug.")
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(@PathVariable String slug) {
        ProductDetailResponse productDetail = productService.getProductDetail(slug);
        ApiResponse<ProductDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy chi tiết sản phẩm thành công",
                productDetail
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy danh sách Sản phẩm Liên quan", description = "Lấy danh sách các sản phẩm cùng danh mục để gợi ý cho người dùng.")
    @GetMapping("/{slug}/related")
    public ResponseEntity<ApiResponse<List<RelatedProductResponse>>> getRelatedProducts(
            @PathVariable String slug,
            @RequestParam(defaultValue = "5") int limit) {
        List<RelatedProductResponse> relatedProducts = productService.getRelatedProducts(slug, limit);
        ApiResponse<List<RelatedProductResponse>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy sản phẩm liên quan thành công",
                relatedProducts
        );
        return ResponseEntity.ok(response);
    }
}

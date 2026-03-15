package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.PagedResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ProductCardResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

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

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductCardResponse>>> getProductsByCategory(
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<ProductCardResponse> products = productService.getValidProductsByCategory(categoryId, page, size);
        ApiResponse<PagedResponse<ProductCardResponse>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy danh sách sản phẩm thành công",
                products
        );
        return ResponseEntity.ok(response);
    }
}

package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request.AdminProductRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.AdminProductDetailResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.AdminProductListResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.PagedResponse;

import java.util.Map;

public interface AdminProductService {
    PagedResponse<AdminProductListResponse> getProductList(int pageNo, int pageSize, String keyword, String categorySlug);
    Map<String, Object> createProduct(AdminProductRequest request);
    AdminProductDetailResponse getProductDetail(String slug);
    Map<String, String> updateProduct(String slug, AdminProductRequest request);
    void toggleProductStatus(String slug);
}

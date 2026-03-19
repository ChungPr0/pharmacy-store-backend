package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request.AdminProductRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.AdminProductDetailResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.AdminProductListResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.PagedResponse;

import java.util.Map;

public interface AdminProductService {
    PagedResponse<AdminProductListResponse> getProductList(int pageNo, int pageSize, String keyword, String categorySlug);
    Map<String, Object> createProduct(AdminProductRequestDTO request);
    AdminProductDetailResponse getProductDetail(String slug);
    Map<String, String> updateProduct(String slug, AdminProductRequestDTO request);
    void toggleProductStatus(String slug);
}

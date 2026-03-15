package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.PagedResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ProductCardResponse;

public interface ProductService {
    PagedResponse<ProductCardResponse> getValidBestSellers(int limit);
    PagedResponse<ProductCardResponse> getValidLatestProducts(int limit);
    PagedResponse<ProductCardResponse> getValidProductsByCategory(Long categoryId, int page, int size);
}

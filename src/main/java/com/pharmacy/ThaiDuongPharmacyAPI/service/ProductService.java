package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.ProductSearchRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.PagedResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ProductCardResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ProductDetailResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ProductSearchResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.RelatedProductResponse;

import java.util.List;

public interface ProductService {
    PagedResponse<ProductCardResponse> getValidBestSellers(int limit);
    PagedResponse<ProductCardResponse> getValidLatestProducts(int limit);
    PagedResponse<ProductSearchResponse> searchProducts(ProductSearchRequestDTO request);
    ProductDetailResponse getProductDetail(String slug);
    List<RelatedProductResponse> getRelatedProducts(String slug, int limit);
}

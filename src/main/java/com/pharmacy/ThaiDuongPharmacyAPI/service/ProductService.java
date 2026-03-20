package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request.ProductSearchRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.ProductCardResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.ProductDetailResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.ProductSearchResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.RelatedProductResponse;

import java.util.List;

public interface ProductService {
    PageResponse<ProductCardResponse> getValidBestSellers(int limit);
    PageResponse<ProductCardResponse> getValidLatestProducts(int limit);
    PageResponse<ProductSearchResponse> searchProducts(ProductSearchRequest request);
    ProductDetailResponse getProductDetail(String slug);
    List<RelatedProductResponse> getRelatedProducts(String slug, int limit);
}

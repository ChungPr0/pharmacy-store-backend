package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request.ProductBatchImportRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.ProductBatchHistoryResponse;

public interface AdminProductBatchService {
    void importProductBatches(ProductBatchImportRequest request);
    PageResponse<ProductBatchHistoryResponse> getProductBatches(int pageNo, int pageSize, Long productId);
}

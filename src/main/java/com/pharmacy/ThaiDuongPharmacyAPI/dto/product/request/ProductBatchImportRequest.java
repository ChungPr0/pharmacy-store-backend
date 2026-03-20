package com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ProductBatchImportRequest {
    private String importNote;

    @NotEmpty(message = "Danh sách lô hàng không được để trống")
    @Valid
    private List<ProductBatchItemRequest> items;
}
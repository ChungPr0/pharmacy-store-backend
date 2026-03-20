package com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ProductBatchHistoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String batchNumber;
    private BigDecimal importPrice;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private Integer stockQuantity;
    private String importNote;
}
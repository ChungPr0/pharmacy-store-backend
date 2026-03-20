package com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductBatchItemRequest {
    @NotNull(message = "ID sản phẩm không được để trống")
    private Long productId;

    @NotBlank(message = "Số lô không được để trống")
    private String batchNumber;

    @Min(value = 0, message = "Giá nhập phải lớn hơn hoặc bằng 0")
    private BigDecimal importPrice;

    @NotNull(message = "Ngày sản xuất không được để trống")
    private LocalDate manufactureDate;

    @NotNull(message = "Ngày hết hạn không được để trống")
    private LocalDate expiryDate;

    @Min(value = 1, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 1")
    private Integer stockQuantity;
}
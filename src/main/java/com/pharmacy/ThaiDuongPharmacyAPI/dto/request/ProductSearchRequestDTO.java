package com.pharmacy.ThaiDuongPharmacyAPI.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProductSearchRequestDTO {
    @NotBlank(message = "Category slug không được để trống")
    private String categorySlug;

    @Min(value = 0, message = "Trang (pageNo) phải bắt đầu từ 0")
    private int pageNo = 0;

    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    @Max(value = 50, message = "Tối đa 50 sản phẩm/trang")
    private int pageSize = 15;

    @Pattern(regexp = "^(price|createdAt|name)$", message = "Chỉ cho phép sort theo: price, createdAt, name")
    private String sortBy = "price";

    @Pattern(regexp = "^(ASC|DESC)$", message = "Sort direction chỉ được là ASC hoặc DESC")
    private String sortDir = "ASC";

    private String keyword = "";
}

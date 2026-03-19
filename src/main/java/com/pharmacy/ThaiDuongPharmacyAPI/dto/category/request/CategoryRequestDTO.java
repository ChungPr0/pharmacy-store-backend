package com.pharmacy.ThaiDuongPharmacyAPI.dto.category.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequestDTO {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    private Long parentId;
}

package com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotNull(message = "Category ID không được để trống")
    private Long categoryId;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá không được nhỏ hơn 0")
    private BigDecimal price;

    private String description;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isActive;

    private List<String> images;

    private List<ProductAttributeRequestDTO> attributes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributeRequestDTO {
        @NotBlank(message = "Tên thuộc tính không được để trống")
        private String name;

        @NotBlank(message = "Giá trị thuộc tính không được để trống")
        private String value;
    }
}

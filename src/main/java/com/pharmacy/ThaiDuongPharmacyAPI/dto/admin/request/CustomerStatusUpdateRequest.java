package com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerStatusUpdateRequest {
    @NotBlank(message = "Trạng thái không được để trống")
    @Pattern(regexp = "^(ACTIVE|BANNED)$", message = "Trạng thái không hợp lệ, chỉ chấp nhận ACTIVE hoặc BANNED")
    private String status;
}

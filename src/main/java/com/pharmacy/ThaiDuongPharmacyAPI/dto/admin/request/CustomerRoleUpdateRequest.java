package com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerRoleUpdateRequest {
    @NotBlank(message = "Vai trò không được để trống")
    @Pattern(regexp = "^(ADMIN|CUSTOMER)$", message = "Vai trò không hợp lệ, chỉ chấp nhận ADMIN hoặc CUSTOMER")
    private String role;
}

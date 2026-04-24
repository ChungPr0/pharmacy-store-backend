package com.pharmacy.ThaiDuongPharmacyAPI.dto.auth.request;

import com.pharmacy.ThaiDuongPharmacyAPI.validation.ValidVietnamesePhone;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Số điện thoại không được để trống")
    @ValidVietnamesePhone
    private String phone;
}

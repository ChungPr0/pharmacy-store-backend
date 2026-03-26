package com.pharmacy.ThaiDuongPharmacyAPI.dto.auth.request;

import com.pharmacy.ThaiDuongPharmacyAPI.validation.ValidVietnamesePhone;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Số điện thoại không được để trống")
    @ValidVietnamesePhone
    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}

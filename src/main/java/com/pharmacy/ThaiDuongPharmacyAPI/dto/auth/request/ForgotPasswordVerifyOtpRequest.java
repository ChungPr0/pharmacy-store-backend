package com.pharmacy.ThaiDuongPharmacyAPI.dto.auth.request;

import com.pharmacy.ThaiDuongPharmacyAPI.validation.ValidVietnamesePhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForgotPasswordVerifyOtpRequest {
    @NotBlank(message = "Số điện thoại không được để trống")
    @ValidVietnamesePhone
    private String phone;

    @NotBlank(message = "Mã OTP không được để trống")
    @Size(min = 6, max = 6, message = "Mã OTP phải có đúng 6 chữ số")
    private String otpCode;
}

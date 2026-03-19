package com.pharmacy.ThaiDuongPharmacyAPI.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequestDTO {
    @NotBlank(message = "Refresh Token không được để trống!")
    private String refreshToken;
}
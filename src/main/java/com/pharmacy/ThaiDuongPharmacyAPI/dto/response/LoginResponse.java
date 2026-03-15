package com.pharmacy.ThaiDuongPharmacyAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String fullName;
    private String role;
}

package com.pharmacy.ThaiDuongPharmacyAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpResponse {
    private String phone;
    private int otpTimeout;
}

package com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCustomerResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String role;
    private String accountStatus;
    private LocalDateTime registrationDate;
}

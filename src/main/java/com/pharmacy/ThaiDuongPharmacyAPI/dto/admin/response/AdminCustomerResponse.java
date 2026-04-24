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
    private String accountStatus;
    // Ghi chú: Yêu cầu nhắc đến "registration date" nhưng Customer / Account hiện tại 
    // không có trường createdAt trong Entity, nên trả về null hoặc không mapping trường này.
    private LocalDateTime registrationDate;
}

package com.pharmacy.ThaiDuongPharmacyAPI.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileResponse {
    private Long id;
    private String fullName;
    private String avatarUrl;
    private String gender;
    private String phone;
    private String email;
    private LocalDate birthday;
    private Integer rewardPoints;
}

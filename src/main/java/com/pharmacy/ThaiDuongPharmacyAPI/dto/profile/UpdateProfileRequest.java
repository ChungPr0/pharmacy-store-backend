package com.pharmacy.ThaiDuongPharmacyAPI.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    private String gender;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    private LocalDate birthday;
}

package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.response.CustomerProfileResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.request.UpdateProfileRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CustomerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profile")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Customer Profile APIs", description = "Các API quản lý thông tin cá nhân của Khách hàng")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    @Operation(summary = "Lấy thông tin cá nhân", description = "Lấy chi tiết thông tin hồ sơ của khách hàng hiện tại")
    @GetMapping
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile() {
        CustomerProfileResponse response = customerProfileService.getProfile();
        return ApiResponse.success("Lấy thông tin khách hàng thành công", response);
    }

    @Operation(summary = "Cập nhật thông tin cá nhân", description = "Cập nhật tên, giới tính, email, ngày sinh của khách hàng")
    @PutMapping
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        CustomerProfileResponse response = customerProfileService.updateProfile(request);
        return ApiResponse.success("Cập nhật thông tin khách hàng thành công", response);
    }

    @Operation(summary = "Cập nhật ảnh đại diện", description = "Upload file ảnh để thay đổi avatar của khách hàng")
    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<String>> updateAvatar(
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = customerProfileService.updateAvatar(file);
        return ApiResponse.success("Cập nhật ảnh đại diện thành công", avatarUrl);
    }
}

package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.response.CustomerProfileResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.request.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerProfileService {
    CustomerProfileResponse getProfile();
    CustomerProfileResponse updateProfile(UpdateProfileRequest request);
    String updateAvatar(MultipartFile file);
}

package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.CustomerProfileResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerProfileService {
    CustomerProfileResponse getProfile();
    CustomerProfileResponse updateProfile(UpdateProfileRequest request);
    String updateAvatar(MultipartFile file);
}

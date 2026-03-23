package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.response.CustomerProfileResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.request.UpdateProfileRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CustomerProfileService;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerRepository customerRepository;
    private final AuthUtils authUtils;

    @Override
    public CustomerProfileResponse getProfile() {
        Customer customer = authUtils.getCurrentCustomer();
        return mapToResponse(customer);
    }

    @Override
    @Transactional
    public CustomerProfileResponse updateProfile(UpdateProfileRequest request) {
        Customer customer = authUtils.getCurrentCustomer();

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (customerRepository.existsByEmailAndIdNot(request.getEmail().trim(), customer.getId())) {
                throw ApiException.badRequest("Email này đã được sử dụng bởi khách hàng khác.");
            }
            customer.setEmail(request.getEmail().trim());
        } else {
            customer.setEmail(null);
        }

        if (request.getFullName() != null) {
            customer.setFullName(request.getFullName().trim());
        }
        
        customer.setGender(request.getGender());
        customer.setBirthday(request.getBirthday());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponse(updatedCustomer);
    }

    @Override
    @Transactional
    public String updateAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("Vui lòng chọn file ảnh hợp lệ.");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw ApiException.badRequest("File tải lên phải là định dạng ảnh.");
        }

        Customer customer = authUtils.getCurrentCustomer();

        // Simulate a cloud upload
        String mockUrl = "https://storage.googleapis.com/thaiduong/mock-avatar-" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        
        customer.setAvatarUrl(mockUrl);
        customerRepository.save(customer);

        return mockUrl;
    }

    private CustomerProfileResponse mapToResponse(Customer customer) {
        return CustomerProfileResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .avatarUrl(customer.getAvatarUrl())
                .gender(customer.getGender())
                .phone(customer.getAccount() != null ? customer.getAccount().getPhone() : null)
                .email(customer.getEmail())
                .birthday(customer.getBirthday())
                .rewardPoints(customer.getRewardPoints())
                .build();
    }
}

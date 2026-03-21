package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.CustomerProfileResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.profile.UpdateProfileRequest;
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

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (customerRepository.existsByEmailAndIdNot(request.getEmail(), customer.getId())) {
                throw ApiException.badRequest("Email này đã được sử dụng bởi khách hàng khác.");
            }
            customer.setEmail(request.getEmail());
        }

        customer.setFullName(request.getFullName());
        customer.setGender(request.getGender());
        customer.setBirthday(request.getBirthday());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponse(updatedCustomer);
    }

    @Override
    @Transactional
    public String updateAvatar(MultipartFile file) {
        Customer customer = authUtils.getCurrentCustomer();

        // Simulate a cloud upload
        String mockUrl = "https://storage.googleapis.com/thaiduong/mock-avatar-" + UUID.randomUUID();
        
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
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .birthday(customer.getBirthday())
                .rewardPoints(customer.getRewardPoints())
                .build();
    }
}

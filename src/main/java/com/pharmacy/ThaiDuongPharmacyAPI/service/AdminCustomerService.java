package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.request.AdminCustomerUpdateRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.request.CustomerStatusUpdateRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.response.AdminCustomerResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;

public interface AdminCustomerService {
    PageResponse<AdminCustomerResponse> getCustomers(int pageNo, int pageSize, String keyword, String status);

    AdminCustomerResponse getCustomerById(Long id);

    AdminCustomerResponse updateCustomer(Long id, AdminCustomerUpdateRequest request);

    void updateCustomerStatus(Long id, CustomerStatusUpdateRequest request);
}

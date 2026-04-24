package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.request.AdminCustomerUpdateRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.request.CustomerStatusUpdateRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.response.AdminCustomerResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.AccountRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AdminCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCustomerServiceImpl implements AdminCustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminCustomerResponse> getCustomers(int pageNo, int pageSize, String keyword, String status) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        if (status != null && status.trim().isEmpty()) {
            status = null;
        }

        Page<Customer> customerPage = customerRepository.findAllAdminCustomers(keyword, status, pageable);

        Page<AdminCustomerResponse> dtoPage = customerPage.map(this::mapToResponse);

        return PageResponse.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminCustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy khách hàng với ID: " + id));
        return mapToResponse(customer);
    }

    @Override
    @Transactional
    public AdminCustomerResponse updateCustomer(Long id, AdminCustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy khách hàng với ID: " + id));

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (customerRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw ApiException.badRequest("Email này đã được sử dụng bởi một khách hàng khác.");
            }
            customer.setEmail(request.getEmail());
        }

        customer.setFullName(request.getFullName());

        // Hiện tại module Customer không lưu trữ trực tiếp text address 
        // Thay vào đó dùng List<CustomerAddress>. API request lại yêu cầu text thuần nên ta sẽ tạm 
        // bỏ qua lưu logic address trực tiếp vào Customer nếu không có structure tương ứng trong database.

        customerRepository.save(customer);

        return mapToResponse(customer);
    }

    @Override
    @Transactional
    public void updateCustomerStatus(Long id, CustomerStatusUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy khách hàng với ID: " + id));

        Account account = customer.getAccount();
        if (account == null) {
            throw ApiException.notFound("Không tìm thấy tài khoản liên kết với khách hàng này.");
        }

        account.setStatus(request.getStatus());
        accountRepository.save(account);
    }

    private AdminCustomerResponse mapToResponse(Customer customer) {
        String defaultAddress = customer.getAddresses().stream()
                .filter(addr -> Boolean.TRUE.equals(addr.getIsDefault()))
                .map(addr -> addr.getDetailAddress() + ", " + addr.getWard() + ", " + addr.getDistrict() + ", " + addr.getProvince())
                .findFirst()
                .orElse(customer.getAddresses().isEmpty() ? "" : 
                    customer.getAddresses().get(0).getDetailAddress() + ", " + 
                    customer.getAddresses().get(0).getWard() + ", " + 
                    customer.getAddresses().get(0).getDistrict() + ", " + 
                    customer.getAddresses().get(0).getProvince()
                );

        return AdminCustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phone(customer.getAccount() != null ? customer.getAccount().getPhone() : null)
                .email(customer.getEmail())
                .address(defaultAddress)
                .accountStatus(customer.getAccount() != null ? customer.getAccount().getStatus() : null)
                .registrationDate(null) // CSDL hiện tại không có entity.createdAt
                .build();
    }
}

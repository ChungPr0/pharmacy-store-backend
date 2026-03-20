package com.pharmacy.ThaiDuongPharmacyAPI.utils;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ResourceNotFoundException;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.UnauthorizedException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.AccountRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public Long getCurrentCustomerId() {
        String phone = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (phone == null) {
            throw new UnauthorizedException("Không tìm thấy thông tin đăng nhập");
        }

        Account account = accountRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        Customer customer = customerRepository.findByAccount(account);
        if (customer == null) {
            throw new ResourceNotFoundException("Thông tin khách hàng không tồn tại");
        }

        return customer.getId();
    }

    public Customer getCurrentCustomer() {
        String phone = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (phone == null) {
            throw new UnauthorizedException("Không tìm thấy thông tin đăng nhập");
        }

        Account account = accountRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        Customer customer = customerRepository.findByAccount(account);
        if (customer == null) {
            throw new ResourceNotFoundException("Thông tin khách hàng không tồn tại");
        }

        return customer;
    }
}

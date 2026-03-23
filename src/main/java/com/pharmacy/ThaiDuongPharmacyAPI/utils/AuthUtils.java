package com.pharmacy.ThaiDuongPharmacyAPI.utils;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.AccountRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public Long getCurrentCustomerId() {
        return getCurrentCustomer().getId();
    }

    public Customer getCurrentCustomer() {
        Object principal = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        String phone;
        if (principal instanceof UserDetails) {
            phone = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            phone = (String) principal;
        } else {
             throw ApiException.unauthorized("Không tìm thấy thông tin đăng nhập");
        }

        Account account = accountRepository.findByPhone(phone)
                .orElseThrow(() -> ApiException.notFound("Tài khoản không tồn tại"));

        Customer customer = customerRepository.findByAccount(account);
        if (customer == null) {
            if ("ADMIN".equals(account.getRole())) {
               Customer adminCustomer = new Customer();
               adminCustomer.setAccount(account);
               adminCustomer.setFullName("Admin");
               return customerRepository.save(adminCustomer);
            }
            throw ApiException.notFound("Thông tin khách hàng không tồn tại");
        }

        return customer;
    }
}
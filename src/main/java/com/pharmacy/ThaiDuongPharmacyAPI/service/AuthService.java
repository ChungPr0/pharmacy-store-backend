package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.RegisterRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.VerifyOtpRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Otp;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.AccountRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public ApiResponse<Object> requestOtp(RegisterRequestDTO request) {
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        Otp otpEntity = new Otp();
        otpEntity.setPhone(request.getPhone());
        otpEntity.setOtpCode(otpCode);
        otpEntity.setExpiresAt(LocalDateTime.now().plusSeconds(60));
        otpEntity.setIsUsed(false);

        otpRepository.save(otpEntity);

        System.out.println("=========================================");
        System.out.println("📲 [MÔ PHỎNG SMS] MÃ OTP CỦA SĐT " + request.getPhone() + " LÀ: " + otpCode);
        System.out.println("=========================================");

        Map<String, Object> data = new HashMap<>();
        data.put("phone", request.getPhone());
        data.put("otpTimeout", 60);

        return new ApiResponse<>(200, "Đã gửi mã OTP đến số điện thoại của bạn.", data);
    }

    public ApiResponse<Object> verifyOtpAndRegister(VerifyOtpRequestDTO request) {
        if (accountRepository.existsByUsername(request.getPhone())) {
            return new ApiResponse<>(400, "Số điện thoại này đã được đăng ký!", null);
        }

        Otp validOtp = otpRepository.findByPhoneAndOtpCodeAndIsUsedFalse(request.getPhone(), request.getOtpCode())
                .orElse(null);

        if (validOtp == null || validOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(400, "Mã OTP không chính xác hoặc đã hết hạn!", null);
        }

        validOtp.setIsUsed(true);
        otpRepository.save(validOtp);

        Customer newCustomer = getNewCustomer(request);

        customerRepository.save(newCustomer);

        Map<String, String> data = new HashMap<>();
        data.put("phone", newCustomer.getPhone());

        return new ApiResponse<>(201, "Đăng ký thành công! Vui lòng đăng nhập.", data);
    }

    private static Customer getNewCustomer(VerifyOtpRequestDTO request) {
        Account newAccount = new Account();
        newAccount.setUsername(request.getPhone());
        newAccount.setPassword(request.getPassword());

        Customer newCustomer = new Customer();
        newCustomer.setAccount(newAccount);
        newCustomer.setFullName(request.getFullName());
        newCustomer.setPhone(request.getPhone());
        newCustomer.setEmail(request.getEmail());
        newCustomer.setAddress(request.getAddress());
        newCustomer.setGender(request.getGender());
        return newCustomer;
    }
}
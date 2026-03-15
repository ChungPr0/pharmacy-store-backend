package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.LoginRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.RegisterRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.TokenRefreshRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.VerifyOtpRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Otp;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.RefreshToken;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.AccountRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.OtpRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.RefreshTokenRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpRepository otpRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    public ApiResponse<Object> login(LoginRequestDTO request) {
        Account account = accountRepository.findByPhone(request.getPhone()).orElse(null);

        if (account == null || !account.getPassword().equals(request.getPassword())) {
            return new ApiResponse<>(401, "Số điện thoại hoặc mật khẩu không chính xác!", null);
        }

        String accessToken = jwtUtils.generateToken(account.getPhone());

        RefreshToken refreshToken = createRefreshToken(account);

        Customer customer = customerRepository.findByAccount(account);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", accessToken);
        responseData.put("refreshToken", refreshToken.getToken());
        responseData.put("type", "Bearer");
        responseData.put("fullName", customer.getFullName());
        responseData.put("role", account.getRole());

        return new ApiResponse<>(200, "Đăng nhập thành công!", responseData);
    }

    public ApiResponse<Object> requestOtp(RegisterRequestDTO request) {
        if (accountRepository.existsByPhone(request.getPhone())) {
            return new ApiResponse<>(400, "Số điện thoại này đã được đăng ký!", null);
        }

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
        if (accountRepository.existsByPhone(request.getPhone())) {
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
        newAccount.setPhone(request.getPhone());
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

    private RefreshToken createRefreshToken(Account account) {
        refreshTokenRepository.deleteByAccount(account);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccount(account);
        refreshToken.setToken(java.util.UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000));

        return refreshTokenRepository.save(refreshToken);
    }

    public ApiResponse<Object> refreshToken(TokenRefreshRequestDTO request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken).orElse(null);

        if (refreshToken == null) {
            return new ApiResponse<>(403, "Refresh Token không tồn tại!", null);
        }

        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            return new ApiResponse<>(403, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", null);
        }

        Account account = refreshToken.getAccount();
        String newAccessToken = jwtUtils.generateToken(account.getPhone());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", newAccessToken);
        responseData.put("refreshToken", refreshToken.getToken());
        responseData.put("type", "Bearer");

        return new ApiResponse<>(200, "Cấp lại Token thành công!", responseData);
    }
}
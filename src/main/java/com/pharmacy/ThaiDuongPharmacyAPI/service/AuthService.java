package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.*;
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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
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

    public ApiResponse<Object> changePassword(ChangePasswordRequestDTO request) {
        String currentPhone = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        Account account = accountRepository.findByPhone(currentPhone)
                .orElse(null);

        if (account == null) {
            return new ApiResponse<>(404, "Không tìm thấy tài khoản!", null);
        }

        if (!account.getPassword().equals(request.getOldPassword())) {
            return new ApiResponse<>(400, "Mật khẩu hiện tại không chính xác!", null);
        }

        if (request.getNewPassword().equals(account.getPassword())) {
            return new ApiResponse<>(400, "Mật khẩu mới không được trùng với mật khẩu hiện tại!", null);
        }

        if (request.getNewPassword().equals(account.getPreviousPassword())) {
            return new ApiResponse<>(400, "Mật khẩu mới đã từng được sử dụng trước đây, vui lòng chọn mật khẩu khác!", null);
        }

        account.setPreviousPassword(account.getPassword());
        account.setPassword(request.getNewPassword());
        accountRepository.save(account);

        refreshTokenRepository.deleteByAccount(account);

        return new ApiResponse<>(200, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", null);
    }

    public ApiResponse<Object> forgotPassword(ForgotPasswordRequestDTO request) {
        Account account = accountRepository.findByPhone(request.getPhone()).orElse(null);
        if (account == null) {
            return new ApiResponse<>(404, "Số điện thoại này chưa được đăng ký!", null);
        }

        String otpCode = String.format("%06d", new Random().nextInt(999999));

        Otp otpEntity = new Otp();
        otpEntity.setPhone(request.getPhone());
        otpEntity.setOtpCode(otpCode);
        otpEntity.setExpiresAt(LocalDateTime.now().plusSeconds(60));
        otpEntity.setIsUsed(false);

        otpRepository.save(otpEntity);

        System.out.println("=========================================");
        System.out.println("📲 [FORGOT PASSWORD] OTP CỦA " + request.getPhone() + " LÀ: " + otpCode);
        System.out.println("=========================================");

        Map<String, Object> data = new HashMap<>();
        data.put("phone", request.getPhone());
        data.put("otpTimeout", 60);

        return new ApiResponse<>(200, "Mã OTP đã được gửi đến số điện thoại của bạn!", data);
    }

    public ApiResponse<Object> resetPassword(ForgotPasswordVerifyOtpRequestDTO request) {
        Otp validOtp = otpRepository.findByPhoneAndOtpCodeAndIsUsedFalse(request.getPhone(), request.getOtpCode())
                .orElse(null);

        if (validOtp == null || validOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(400, "Mã OTP không chính xác hoặc đã hết hạn!", null);
        }

        Account account = accountRepository.findByPhone(request.getPhone()).orElse(null);
        if (account == null) {
            return new ApiResponse<>(404, "Tài khoản không tồn tại!", null);
        }

        validOtp.setIsUsed(true);
        otpRepository.save(validOtp);

        account.setPreviousPassword(account.getPassword());
        account.setPassword(request.getPassword());
        accountRepository.save(account);

        refreshTokenRepository.deleteByAccount(account);

        return new ApiResponse<>(200, "Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.", null);
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
        RefreshToken refreshToken = refreshTokenRepository.findByAccount(account)
                .orElse(new RefreshToken());

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
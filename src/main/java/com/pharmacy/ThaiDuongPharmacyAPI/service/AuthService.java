package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.auth.request.*;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.auth.response.*;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Otp;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.RefreshToken;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.*;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.AccountRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.OtpRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.RefreshTokenRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
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
    private final PasswordEncoder passwordEncoder;

    private static final int OTP_EXPIRATION_SECONDS = 60;

    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new UnauthorizedException("Số điện thoại hoặc mật khẩu không chính xác!"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new UnauthorizedException("Số điện thoại hoặc mật khẩu không chính xác!");
        }

        String accessToken = jwtUtils.generateToken(account.getPhone());
        RefreshToken refreshToken = createRefreshToken(account);
        Customer customer = customerRepository.findByAccount(account);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                customer != null ? customer.getFullName() : null,
                account.getRole()
        );
    }

    public void changePassword(ChangePasswordRequest request) {
        String currentPhone = (String) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        Account account = accountRepository.findByPhone(currentPhone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản!"));

        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new BadRequestException("Mật khẩu hiện tại không chính xác!");
        }

        if (passwordEncoder.matches(request.getNewPassword(), account.getPassword())) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu hiện tại!");
        }

        if (account.getPreviousPassword() != null && passwordEncoder.matches(request.getNewPassword(), account.getPreviousPassword())) {
            throw new BadRequestException("Mật khẩu mới đã từng được sử dụng trước đây, vui lòng chọn mật khẩu khác!");
        }

        account.setPreviousPassword(account.getPassword());
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        refreshTokenRepository.deleteByAccount(account);
    }

    public OtpResponse forgotPasswordRequestOtp(ForgotPasswordRequest request) {
        if (!accountRepository.existsByPhone(request.getPhone())) {
            throw new ResourceNotFoundException("Số điện thoại này chưa được đăng ký!");
        }
        
        sendOtp(request.getPhone(), "FORGOT PASSWORD");
        return new OtpResponse(request.getPhone(), OTP_EXPIRATION_SECONDS);
    }

    public void forgotPasswordVerifyOtp(ForgotPasswordVerifyOtpRequest request) {
        Otp validOtp = otpRepository.findByPhoneAndOtpCodeAndIsUsedFalse(request.getPhone(), request.getOtpCode())
                .orElseThrow(() -> new BadRequestException("Mã OTP không chính xác hoặc đã hết hạn!"));

        if (validOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP không chính xác hoặc đã hết hạn!");
        }
    }

    public void forgotPasswordReset(ForgotPasswordResetRequest request) {
        Otp validOtp = otpRepository.findByPhoneAndOtpCodeAndIsUsedFalse(request.getPhone(), request.getOtpCode())
                .orElseThrow(() -> new BadRequestException("Mã OTP không chính xác hoặc đã hết hạn!"));

        if (validOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP không chính xác hoặc đã hết hạn!");
        }

        Account account = accountRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));

        account.setPreviousPassword(account.getPassword());
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        validOtp.setIsUsed(true);
        otpRepository.save(validOtp);

        refreshTokenRepository.deleteByAccount(account);
    }

    public OtpResponse requestOtp(RegisterRequest request) {
        if (accountRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Số điện thoại này đã được đăng ký!");
        }

        sendOtp(request.getPhone(), "REGISTER");
        return new OtpResponse(request.getPhone(), OTP_EXPIRATION_SECONDS);
    }

    public RegisterResponse verifyOtpAndRegister(RegisterVerifyOtpRequest request) {
        if (accountRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Số điện thoại này đã được đăng ký!");
        }

        Otp validOtp = otpRepository.findByPhoneAndOtpCodeAndIsUsedFalse(request.getPhone(), request.getOtpCode())
                .orElseThrow(() -> new BadRequestException("Mã OTP không chính xác hoặc đã hết hạn!"));

        if (validOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP không chính xác hoặc đã hết hạn!");
        }

        validOtp.setIsUsed(true);
        otpRepository.save(validOtp);

        Customer newCustomer = getNewCustomer(request);
        customerRepository.save(newCustomer);

        return new RegisterResponse(newCustomer.getPhone());
    }

    private void sendOtp(String phone, String type) {
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        Otp otpEntity = new Otp();
        otpEntity.setPhone(phone);
        otpEntity.setOtpCode(otpCode);
        otpEntity.setExpiresAt(LocalDateTime.now().plusSeconds(OTP_EXPIRATION_SECONDS));
        otpEntity.setIsUsed(false);
        otpRepository.save(otpEntity);

        System.out.println("=========================================");
        System.out.printf("📲 [%s] OTP CỦA %s LÀ: %s%n", type, phone, otpCode);
        System.out.println("=========================================");
    }

    private Customer getNewCustomer(RegisterVerifyOtpRequest request) {
        Account newAccount = new Account();
        newAccount.setPhone(request.getPhone());
        newAccount.setPassword(passwordEncoder.encode(request.getPassword()));

        Customer newCustomer = new Customer();
        newCustomer.setAccount(newAccount);
        newCustomer.setFullName(request.getFullName());
        newCustomer.setPhone(request.getPhone());
        newCustomer.setEmail(request.getEmail());
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

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new ForbiddenException("Refresh Token không tồn tại!"));

        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new ForbiddenException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
        }

        Account account = refreshToken.getAccount();
        String newAccessToken = jwtUtils.generateToken(account.getPhone());

        return new TokenRefreshResponse(newAccessToken, refreshToken.getToken(), "Bearer");
    }
}

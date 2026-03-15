package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.*;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.*;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponse loginResponse = authService.login(request);
        ApiResponse<LoginResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Đăng nhập thành công!", loginResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> requestForgotPasswordOtp(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        OtpResponse otpResponse = authService.forgotPassword(request);
        ApiResponse<OtpResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Mã OTP đã được gửi đến số điện thoại của bạn!", otpResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyForgotPasswordOtp(@Valid @RequestBody ForgotPasswordVerifyOtpRequestDTO request) {
        authService.resetPassword(request);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/request-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> requestOtp(@Valid @RequestBody RegisterRequestDTO request) {
        OtpResponse otpResponse = authService.requestOtp(request);
        ApiResponse<OtpResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Đã gửi mã OTP đến số điện thoại của bạn.", otpResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<ApiResponse<RegisterResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        RegisterResponse registerResponse = authService.verifyOtpAndRegister(request);
        ApiResponse<RegisterResponse> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Đăng ký thành công! Vui lòng đăng nhập.", registerResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request) {
        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(request);
        ApiResponse<TokenRefreshResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Cấp lại Token thành công!", tokenRefreshResponse);
        return ResponseEntity.ok(response);
    }
}

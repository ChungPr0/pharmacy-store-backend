package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.*;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.*;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@Tag(name = "Authentication APIs", description = "Các API liên quan đến Xác thực (Đăng nhập, Đăng ký, OTP, Đổi mật khẩu)")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Đăng nhập", description = "Xác thực người dùng bằng email/số điện thoại và mật khẩu, trả về Access Token và Refresh Token.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponse loginResponse = authService.login(request);
        ApiResponse<LoginResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Đăng nhập thành công!", loginResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Đổi mật khẩu", description = "Cho phép người dùng đã đăng nhập đổi sang mật khẩu mới. Yêu cầu truyền Bearer Token trong Header.")
    @PostMapping("/change-password")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "BƯỚC 1 - Quên mật khẩu: Yêu cầu mã OTP", description = "Kiểm tra số điện thoại có tồn tại không và gửi mã OTP 6 số (có hiệu lực 60s).")
    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> requestForgotPasswordOtp(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        OtpResponse otpResponse = authService.forgotPasswordRequestOtp(request);
        ApiResponse<OtpResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Mã OTP đã được gửi đến số điện thoại của bạn!", otpResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "BƯỚC 2 - Quên mật khẩu: Xác thực OTP", description = "Kiểm tra mã OTP xem có khớp và còn hạn không. (Lưu ý: Chưa reset mật khẩu ở bước này)")
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyForgotPasswordOtp(@Valid @RequestBody ForgotPasswordVerifyOtpRequestDTO request) {
        authService.forgotPasswordVerifyOtp(request);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Mã OTP hợp lệ!", null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "BƯỚC 3 - Quên mật khẩu: Đặt lại mật khẩu", description = "Kiểm tra lại OTP lần cuối, đổi mật khẩu mới và vô hiệu hóa OTP để không bị dùng lại.")
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ForgotPasswordResetRequestDTO request) {
        authService.forgotPasswordReset(request);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.", null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Đăng ký: Yêu cầu mã OTP", description = "Bắt đầu luồng đăng ký bằng cách gửi mã OTP xác thực số điện thoại.")
    @PostMapping("/register/request-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> requestOtp(@Valid @RequestBody RegisterRequestDTO request) {
        OtpResponse otpResponse = authService.requestOtp(request);
        ApiResponse<OtpResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Đã gửi mã OTP đến số điện thoại của bạn.", otpResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Đăng ký: Xác thực OTP", description = "Hoàn tất luồng đăng ký bằng cách cung cấp OTP đúng. Trả về thông tin đăng ký thành công.")
    @PostMapping("/register/verify-otp")
    public ResponseEntity<ApiResponse<RegisterResponse>> verifyOtp(@Valid @RequestBody RegisterVerifyOtpRequestDTO request) {
        RegisterResponse registerResponse = authService.verifyOtpAndRegister(request);
        ApiResponse<RegisterResponse> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Đăng ký thành công! Vui lòng đăng nhập.", registerResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Làm mới Token (Refresh Token)", description = "Sử dụng Refresh Token để lấy một Access Token mới khi Access Token cũ hết hạn.")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request) {
        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(request);
        ApiResponse<TokenRefreshResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Cấp lại Token thành công!", tokenRefreshResponse);
        return ResponseEntity.ok(response);
    }
}

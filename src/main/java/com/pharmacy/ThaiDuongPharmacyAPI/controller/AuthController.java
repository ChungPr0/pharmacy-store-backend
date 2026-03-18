package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.*;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.*;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
        return ApiResponse.success("Đăng nhập thành công!", authService.login(request));
    }

    @Operation(summary = "Đổi mật khẩu", description = "Cho phép người dùng đã đăng nhập đổi sang mật khẩu mới. Yêu cầu truyền Bearer Token trong Header.")
    @PostMapping("/change-password")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request);
        return ApiResponse.success("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
    }

    @Operation(summary = "BƯỚC 1 - Quên mật khẩu: Yêu cầu mã OTP", description = "Kiểm tra số điện thoại có tồn tại không và gửi mã OTP 6 số (có hiệu lực 60s).")
    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> requestForgotPasswordOtp(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        return ApiResponse.success("Mã OTP đã được gửi đến số điện thoại của bạn!", authService.forgotPasswordRequestOtp(request));
    }

    @Operation(summary = "BƯỚC 2 - Quên mật khẩu: Xác thực OTP", description = "Kiểm tra mã OTP xem có khớp và còn hạn không. (Lưu ý: Chưa reset mật khẩu ở bước này)")
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyForgotPasswordOtp(@Valid @RequestBody ForgotPasswordVerifyOtpRequestDTO request) {
        authService.forgotPasswordVerifyOtp(request);
        return ApiResponse.success("Mã OTP hợp lệ!");
    }

    @Operation(summary = "BƯỚC 3 - Quên mật khẩu: Đặt lại mật khẩu", description = "Kiểm tra lại OTP lần cuối, đổi mật khẩu mới và vô hiệu hóa OTP để không bị dùng lại.")
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ForgotPasswordResetRequestDTO request) {
        authService.forgotPasswordReset(request);
        return ApiResponse.success("Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.");
    }

    @Operation(summary = "Đăng ký: Yêu cầu mã OTP", description = "Bắt đầu luồng đăng ký bằng cách gửi mã OTP xác thực số điện thoại.")
    @PostMapping("/register/request-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> requestOtp(@Valid @RequestBody RegisterRequestDTO request) {
        return ApiResponse.success("Đã gửi mã OTP đến số điện thoại của bạn.", authService.requestOtp(request));
    }

    @Operation(summary = "Đăng ký: Xác thực OTP", description = "Hoàn tất luồng đăng ký bằng cách cung cấp OTP đúng. Trả về thông tin đăng ký thành công.")
    @PostMapping("/register/verify-otp")
    public ResponseEntity<ApiResponse<RegisterResponse>> verifyOtp(@Valid @RequestBody RegisterVerifyOtpRequestDTO request) {
        return ApiResponse.created("Đăng ký thành công! Vui lòng đăng nhập.", authService.verifyOtpAndRegister(request));
    }

    @Operation(summary = "Làm mới Token (Refresh Token)", description = "Sử dụng Refresh Token để lấy một Access Token mới khi Access Token cũ hết hạn.")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request) {
        return ApiResponse.success("Cấp lại Token thành công!", authService.refreshToken(request));
    }
}
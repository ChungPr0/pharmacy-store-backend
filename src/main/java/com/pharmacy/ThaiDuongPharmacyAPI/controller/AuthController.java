package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.LoginRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.RegisterRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.TokenRefreshRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.VerifyOtpRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequestDTO request) {
        ApiResponse<Object> response = authService.login(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/register/request-otp")
    public ResponseEntity<ApiResponse<Object>> requestOtp(@Valid @RequestBody RegisterRequestDTO request) {
        ApiResponse<Object> response = authService.requestOtp(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        ApiResponse<Object> response = authService.verifyOtpAndRegister(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Object>> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request) {
        ApiResponse<Object> response = authService.refreshToken(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
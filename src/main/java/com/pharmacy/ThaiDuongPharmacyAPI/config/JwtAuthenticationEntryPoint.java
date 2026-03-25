package com.pharmacy.ThaiDuongPharmacyAPI.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import lombok.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> apiResponse = new ApiResponse<>(HttpServletResponse.SC_UNAUTHORIZED, "Yêu cầu xác thực để truy cập!", null);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}

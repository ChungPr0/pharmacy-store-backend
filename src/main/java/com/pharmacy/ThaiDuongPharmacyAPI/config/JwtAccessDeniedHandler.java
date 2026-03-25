package com.pharmacy.ThaiDuongPharmacyAPI.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import lombok.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> apiResponse = new ApiResponse<>(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập vào tài nguyên này!", null);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}

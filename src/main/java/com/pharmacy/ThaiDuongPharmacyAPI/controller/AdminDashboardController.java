package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.dashboard.ChartDataResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard APIs", description = "Các API thống kê Dashboard dành cho Quản trị viên (Admin)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Lấy dữ liệu biểu đồ", description = "Lấy dữ liệu thống kê theo ngày (ORDERS, REVENUE, PRODUCTS, CUSTOMERS)")
    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<List<ChartDataResponse>>> getChartData(
            @RequestParam("type") String type,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        return ApiResponse.success("Lấy dữ liệu biểu đồ thành công", dashboardService.getChartData(type, from, to));
    }
}

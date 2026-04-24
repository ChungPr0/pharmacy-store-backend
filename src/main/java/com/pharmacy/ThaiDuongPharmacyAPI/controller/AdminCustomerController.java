package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.request.AdminCustomerUpdateRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.request.CustomerStatusUpdateRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.admin.response.AdminCustomerResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AdminCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/customers")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Admin Customer APIs", description = "Các API dành cho Admin quản lý khách hàng")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminCustomerController {

    private final AdminCustomerService adminCustomerService;

    @Operation(summary = "Lấy Danh Sách Khách Hàng Có Phân Trang", description = "Lấy danh sách khách hàng dưới dạng phân trang, hỗ trợ tìm kiếm theo tên/số điện thoại và lọc theo trạng thái hoạt động.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AdminCustomerResponse>>> getCustomers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
        
        return ApiResponse.success("Lấy danh sách khách hàng thành công", 
                adminCustomerService.getCustomers(pageNo, pageSize, keyword, status));
    }

    @Operation(summary = "Lấy Chi Tiết Một Khách Hàng", description = "Lấy toàn bộ thông tin của một khách hàng cụ thể khi Admin bấm vào nút xem chi tiết hoặc muốn lấy data cũ đổ vào form cập nhật.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminCustomerResponse>> getCustomerById(@PathVariable Long id) {
        return ApiResponse.success("Lấy thông tin khách hàng thành công", 
                adminCustomerService.getCustomerById(id));
    }

    @Operation(summary = "Cập Nhật Thông Tin Khách Hàng", description = "Cho phép Admin sửa đổi một số thông tin cơ bản của khách hàng (như tên, email, địa chỉ) trong trường hợp khách hàng gọi lên tổng đài nhờ hỗ trợ. Không cho phép đổi số điện thoại.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminCustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody AdminCustomerUpdateRequest request) {
        
        return ApiResponse.success("Cập nhật thông tin khách hàng thành công", 
                adminCustomerService.updateCustomer(id, request));
    }

    @Operation(summary = "Khóa/Mở Khóa Tài Khoản", description = "Thay đổi trạng thái của tài khoản. Admin gọi API này để cấm khách hàng truy cập hệ thống khi phát hiện gian lận (BANNED) hoặc gỡ lệnh cấm (ACTIVE).")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateCustomerStatus(
            @PathVariable Long id,
            @Valid @RequestBody CustomerStatusUpdateRequest request) {
        
        adminCustomerService.updateCustomerStatus(id, request);
        return ApiResponse.success("Cập nhật trạng thái tài khoản thành công");
    }
}

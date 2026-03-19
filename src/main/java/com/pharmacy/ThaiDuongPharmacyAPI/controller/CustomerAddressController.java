package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.address.request.AddressRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.address.response.AddressResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CustomerAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Customer Address APIs", description = "Các API quản lý sổ địa chỉ của Khách hàng")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerAddressController {

    private final CustomerAddressService addressService;

    @Operation(summary = "Lấy danh sách địa chỉ", description = "Lấy toàn bộ địa chỉ của khách hàng hiện tại, địa chỉ mặc định luôn ở đầu danh sách.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {
        return ApiResponse.success("Lấy danh sách địa chỉ thành công", addressService.getMyAddresses());
    }

    @Operation(summary = "Thêm mới địa chỉ", description = "Thêm một địa chỉ mới vào sổ địa chỉ. Nếu là địa chỉ đầu tiên, tự động thành mặc định.")
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(@Valid @RequestBody AddressRequestDTO requestDTO) {
        return ApiResponse.created("Thêm mới địa chỉ thành công", addressService.createAddress(requestDTO));
    }

    @Operation(summary = "Cập nhật địa chỉ", description = "Cập nhật thông tin địa chỉ theo ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDTO requestDTO) {
        return ApiResponse.success("Cập nhật địa chỉ thành công", addressService.updateAddress(id, requestDTO));
    }

    @Operation(summary = "Xóa địa chỉ", description = "Xóa một địa chỉ khỏi sổ địa chỉ. Nếu địa chỉ mặc định bị xóa, địa chỉ khác sẽ được chọn làm mặc định mới.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.success("Xóa địa chỉ thành công");
    }

    @Operation(summary = "Thiết lập địa chỉ mặc định", description = "Chuyển một địa chỉ cụ thể thành địa chỉ giao hàng mặc định.")
    @PatchMapping("/{id}/default")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(@PathVariable Long id) {
        addressService.setDefaultAddress(id);
        return ApiResponse.success("Thiết lập địa chỉ mặc định thành công");
    }
}

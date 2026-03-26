package com.pharmacy.ThaiDuongPharmacyAPI.dto.address.request;

import com.pharmacy.ThaiDuongPharmacyAPI.validation.ValidVietnamesePhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @ValidVietnamesePhone
    private String phoneNumber;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String province;

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;

    @NotBlank(message = "Phường/Xã không được để trống")
    private String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 255, message = "Địa chỉ chi tiết quá dài")
    private String detailAddress;

    private Boolean isDefault = false;
}

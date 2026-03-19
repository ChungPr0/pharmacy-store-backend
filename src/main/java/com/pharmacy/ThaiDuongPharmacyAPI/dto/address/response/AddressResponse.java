package com.pharmacy.ThaiDuongPharmacyAPI.dto.address.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.CustomerAddress;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private String receiverName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detailAddress;
    private Boolean isDefault;

    public static AddressResponse fromEntity(CustomerAddress entity) {
        return AddressResponse.builder()
                .id(entity.getId())
                .receiverName(entity.getReceiverName())
                .phoneNumber(entity.getPhoneNumber())
                .province(entity.getProvince())
                .district(entity.getDistrict())
                .ward(entity.getWard())
                .detailAddress(entity.getDetailAddress())
                .isDefault(entity.getIsDefault())
                .build();
    }
}

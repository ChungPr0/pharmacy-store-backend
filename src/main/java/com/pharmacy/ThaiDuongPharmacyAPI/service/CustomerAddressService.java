package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.address.request.AddressRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.address.response.AddressResponse;

import java.util.List;

public interface CustomerAddressService {
    List<AddressResponse> getMyAddresses();
    AddressResponse createAddress(AddressRequest requestDTO);
    AddressResponse updateAddress(Long id, AddressRequest requestDTO);
    void deleteAddress(Long id);
    void setDefaultAddress(Long id);
}

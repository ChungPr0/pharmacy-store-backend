package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.address.request.AddressRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.address.response.AddressResponse;

import java.util.List;

public interface CustomerAddressService {
    List<AddressResponse> getMyAddresses();
    AddressResponse createAddress(AddressRequestDTO requestDTO);
    AddressResponse updateAddress(Long id, AddressRequestDTO requestDTO);
    void deleteAddress(Long id);
    void setDefaultAddress(Long id);
}

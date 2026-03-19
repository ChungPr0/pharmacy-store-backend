package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.address.request.AddressRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.address.response.AddressResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.CustomerAddress;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ForbiddenException;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ResourceNotFoundException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerAddressRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CustomerAddressService;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerAddressServiceImpl implements CustomerAddressService {

    private final CustomerAddressRepository addressRepository;
    private final AuthUtils authUtils;

    @Override
    public List<AddressResponse> getMyAddresses() {
        Long customerId = authUtils.getCurrentCustomerId();
        List<CustomerAddress> addresses = addressRepository.findByCustomerIdOrderByIsDefaultDescIdDesc(customerId);
        return addresses.stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse createAddress(AddressRequest requestDTO) {
        Customer currentCustomer = authUtils.getCurrentCustomer();
        Long customerId = currentCustomer.getId();

        boolean hasExistingAddresses = addressRepository.existsByCustomerId(customerId);
        boolean isDefault = requestDTO.getIsDefault() != null && requestDTO.getIsDefault();

        if (!hasExistingAddresses) {
            isDefault = true; // Rule 1
        } else if (isDefault) {
            addressRepository.resetDefaultAddressForCustomer(customerId); // Rule 2
        }

        CustomerAddress address = new CustomerAddress();
        address.setCustomer(currentCustomer);
        mapRequestToEntity(requestDTO, address);
        address.setIsDefault(isDefault);

        CustomerAddress savedAddress = addressRepository.save(address);
        return AddressResponse.fromEntity(savedAddress);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest requestDTO) {
        Long customerId = authUtils.getCurrentCustomerId();
        CustomerAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        if (!address.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa địa chỉ này");
        }

        boolean isDefaultRequest = requestDTO.getIsDefault() != null && requestDTO.getIsDefault();

        // If trying to set as default, reset others first
        if (isDefaultRequest && !address.getIsDefault()) {
            addressRepository.resetDefaultAddressForCustomer(customerId);
        }

        mapRequestToEntity(requestDTO, address);

        boolean isDefault = isDefaultRequest;
        if (!isDefaultRequest) {
             List<CustomerAddress> existingAddresses = addressRepository.findByCustomerIdOrderByIsDefaultDescIdDesc(customerId);
             if (existingAddresses.size() == 1) {
                  // If it's the only one, it must be default
                  isDefault = true;
             } else if (address.getIsDefault()) {
                  // Cannot disable default without setting another one default
                  isDefault = true; 
             }
        }

        address.setIsDefault(isDefault);

        CustomerAddress savedAddress = addressRepository.save(address);
        return AddressResponse.fromEntity(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        Long customerId = authUtils.getCurrentCustomerId();
        CustomerAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        if (!address.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("Bạn không có quyền xóa địa chỉ này");
        }

        boolean wasDefault = address.getIsDefault();
        addressRepository.delete(address);
        
        addressRepository.flush(); // Flush to make sure it's gone from DB before checking the next one

        // If it was default, set another to default
        if (wasDefault) {
            addressRepository.findFirstByCustomerIdOrderByIdDesc(customerId).ifPresent(newDefault -> {
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            });
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long id) {
        Long customerId = authUtils.getCurrentCustomerId();
        CustomerAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        if (!address.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("Bạn không có quyền thay đổi địa chỉ này");
        }

        if (address.getIsDefault()) {
            return; // Already default
        }

        addressRepository.resetDefaultAddressForCustomer(customerId);
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    private void mapRequestToEntity(AddressRequest requestDTO, CustomerAddress address) {
        address.setReceiverName(requestDTO.getReceiverName());
        address.setPhoneNumber(requestDTO.getPhoneNumber());
        address.setProvince(requestDTO.getProvince());
        address.setDistrict(requestDTO.getDistrict());
        address.setWard(requestDTO.getWard());
        address.setDetailAddress(requestDTO.getDetailAddress());
    }
}

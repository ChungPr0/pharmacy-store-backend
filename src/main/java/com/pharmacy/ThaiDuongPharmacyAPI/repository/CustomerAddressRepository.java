package com.pharmacy.ThaiDuongPharmacyAPI.repository;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

    List<CustomerAddress> findByCustomerIdOrderByIsDefaultDescIdDesc(Long customerId);
    
    boolean existsByCustomerId(Long customerId);

    @Modifying
    @Query("UPDATE CustomerAddress c SET c.isDefault = false WHERE c.customer.id = :customerId")
    void resetDefaultAddressForCustomer(@Param("customerId") Long customerId);

    Optional<CustomerAddress> findFirstByCustomerIdOrderByIdDesc(Long customerId);
}

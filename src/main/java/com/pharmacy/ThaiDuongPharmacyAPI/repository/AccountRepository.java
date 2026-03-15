package com.pharmacy.ThaiDuongPharmacyAPI.repository;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
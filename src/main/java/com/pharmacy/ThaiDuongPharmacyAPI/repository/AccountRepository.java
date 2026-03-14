package com.pharmacy.ThaiDuongPharmacyAPI.repository;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByUsername(String username);
}
package com.pharmacy.ThaiDuongPharmacyAPI.repository;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByPhone(String phone);
    boolean existsByPhone(String phone);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DATE(created_at) as date, COUNT(id) as count " +
            "FROM accounts " +
            "WHERE role = 'CUSTOMER' AND created_at >= :startDate AND created_at <= :endDate " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date ASC", nativeQuery = true)
    java.util.List<Object[]> countCustomersByDate(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, 
                                                  @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);
}
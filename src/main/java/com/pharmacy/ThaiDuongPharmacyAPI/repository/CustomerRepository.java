package com.pharmacy.ThaiDuongPharmacyAPI.repository;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByAccount(Account account);
    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT c FROM Customer c JOIN c.account a WHERE " +
           "(:keyword IS NULL OR c.fullName LIKE %:keyword% OR c.email LIKE %:keyword% OR a.phone LIKE %:keyword%) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Customer> findAllAdminCustomers(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);
}

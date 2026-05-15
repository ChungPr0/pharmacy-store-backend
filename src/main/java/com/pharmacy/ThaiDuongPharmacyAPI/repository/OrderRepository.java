package com.pharmacy.ThaiDuongPharmacyAPI.repository;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Order> findByOrderCode(String orderCode);

    long countByStatusIn(List<Order.OrderStatus> statuses);

    List<Order> findTop5ByStatusInOrderByCreatedAtDesc(List<Order.OrderStatus> statuses);



    @org.springframework.data.jpa.repository.Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
           "FROM Order o JOIN o.items i " +
           "WHERE o.customer.id = :customerId " +
           "AND i.product.id = :productId " +
           "AND o.status = com.pharmacy.ThaiDuongPharmacyAPI.entity.Order.OrderStatus.DELIVERED")
    boolean hasCustomerPurchasedProduct(
            @org.springframework.data.repository.query.Param("customerId") Long customerId, 
            @org.springframework.data.repository.query.Param("productId") Long productId);
}

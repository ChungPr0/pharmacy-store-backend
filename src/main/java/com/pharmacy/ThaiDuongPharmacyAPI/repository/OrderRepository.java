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

    @org.springframework.data.jpa.repository.Query(value = "SELECT DATE(created_at) as date, COUNT(id) as count " +
            "FROM orders " +
            "WHERE created_at >= :startDate AND created_at <= :endDate " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date ASC", nativeQuery = true)
    List<Object[]> countOrdersByDate(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, 
                                     @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DATE(created_at) as date, SUM(total_amount) as total " +
            "FROM orders " +
            "WHERE created_at >= :startDate AND created_at <= :endDate AND status = 'DELIVERED' " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date ASC", nativeQuery = true)
    List<Object[]> sumRevenueByDate(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, 
                                    @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DATE(o.created_at) as date, SUM(oi.quantity) as count " +
            "FROM orders o JOIN order_items oi ON o.id = oi.order_id " +
            "WHERE o.created_at >= :startDate AND o.created_at <= :endDate AND o.status = 'DELIVERED' " +
            "GROUP BY DATE(o.created_at) " +
            "ORDER BY date ASC", nativeQuery = true)
    List<Object[]> sumProductsSoldByDate(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, 
                                         @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);
}

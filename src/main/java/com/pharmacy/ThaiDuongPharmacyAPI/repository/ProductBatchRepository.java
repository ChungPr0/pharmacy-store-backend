package com.pharmacy.ThaiDuongPharmacyAPI.repository;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {
    
    @Query("SELECT pb FROM ProductBatch pb WHERE pb.product.id = :productId AND pb.stockQuantity > 0 ORDER BY pb.expiryDate ASC")
    List<ProductBatch> findAvailableBatchesByProductIdOrderByExpiryDateAsc(@Param("productId") Long productId);
}

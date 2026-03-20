package com.pharmacy.ThaiDuongPharmacyAPI.repository;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.ProductBatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {

    @Query("SELECT pb FROM ProductBatch pb JOIN FETCH pb.product p WHERE (:productId IS NULL OR p.id = :productId)")
    Page<ProductBatch> findHistoryWithFilters(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT pb FROM ProductBatch pb WHERE pb.product.id = :productId AND pb.stockQuantity > 0 ORDER BY pb.expiryDate ASC")
    List<ProductBatch> findAvailableBatchesByProductIdOrderByExpiryDateAsc(@Param("productId") Long productId);
}

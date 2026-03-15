package com.pharmacy.ThaiDuongPharmacyAPI.repository;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Get best sellers that are valid
    // Note: p.isActive = true ensures product is not hidden by admin
    // Note: pb.expiryDate > :minExpiryDate ensures we don't sell medicines that are about to expire soon
    @Query("SELECT p FROM Product p WHERE p.isBestSeller = true AND p.isActive = true AND EXISTS (" +
           "SELECT 1 FROM ProductBatch pb WHERE pb.product = p AND pb.stockQuantity > 0 AND pb.expiryDate > :minExpiryDate)")
    Page<Product> findValidBestSellers(@Param("minExpiryDate") LocalDate minExpiryDate, Pageable pageable);

    // 2. Get latest products that are valid
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND EXISTS (" +
           "SELECT 1 FROM ProductBatch pb WHERE pb.product = p AND pb.stockQuantity > 0 AND pb.expiryDate > :minExpiryDate) " +
           "ORDER BY p.createdAt DESC")
    Page<Product> findValidLatestProducts(@Param("minExpiryDate") LocalDate minExpiryDate, Pageable pageable);

    // 3. Get products by category (and its subcategories) that are valid
    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds AND p.isActive = true AND EXISTS (" +
           "SELECT 1 FROM ProductBatch pb WHERE pb.product = p AND pb.stockQuantity > 0 AND pb.expiryDate > :minExpiryDate)")
    Page<Product> findValidProductsByCategoryIds(
            @Param("categoryIds") java.util.List<Long> categoryIds,
            @Param("minExpiryDate") LocalDate minExpiryDate,
            Pageable pageable);
}

package com.pharmacy.ThaiDuongPharmacyAPI.repository;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ProductSearchResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.RelatedProductResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.isBestSeller = true AND p.isActive = true AND EXISTS (" +
           "SELECT 1 FROM ProductBatch pb WHERE pb.product = p AND pb.stockQuantity > 0 AND pb.expiryDate > :minExpiryDate)")
    Page<Product> findValidBestSellers(@Param("minExpiryDate") LocalDate minExpiryDate, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND EXISTS (" +
           "SELECT 1 FROM ProductBatch pb WHERE pb.product = p AND pb.stockQuantity > 0 AND pb.expiryDate > :minExpiryDate) " +
           "ORDER BY p.createdAt DESC")
    Page<Product> findValidLatestProducts(@Param("minExpiryDate") LocalDate minExpiryDate, Pageable pageable);

    @Query("SELECT new com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ProductSearchResponse(" +
           "p.id, p.name, p.slug, p.imageUrl, p.price, p.isBestSeller, COALESCE(SUM(pb.stockQuantity), 0L)) " +
           "FROM Product p " +
           "LEFT JOIN p.batches pb " +
           "WHERE p.category.id IN :categoryIds " +
           "AND p.isActive = true " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "GROUP BY p.id, p.name, p.slug, p.imageUrl, p.price, p.isBestSeller")
    Page<ProductSearchResponse> searchProductsByCategoryIdsAndKeyword(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT new com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ProductSearchResponse(" +
           "p.id, p.name, p.slug, p.imageUrl, p.price, p.isBestSeller, COALESCE(SUM(pb.stockQuantity), 0L)) " +
           "FROM Product p " +
           "LEFT JOIN p.batches pb " +
           "WHERE p.isActive = true " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "GROUP BY p.id, p.name, p.slug, p.imageUrl, p.price, p.isBestSeller")
    Page<ProductSearchResponse> searchProductsGloballyByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.images " +
           "WHERE p.slug = :slug AND p.isActive = true")
    Optional<Product> findBySlugWithDetails(@Param("slug") String slug);

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.attributes " +
           "WHERE p.id = :productId")
    Optional<Product> findProductAttributesById(@Param("productId") Long productId);

    @Query("SELECT COALESCE(SUM(pb.stockQuantity), 0) FROM ProductBatch pb WHERE pb.product.id = :productId")
    Long getTotalStockQuantityByProductId(@Param("productId") Long productId);

    @Query("SELECT new com.pharmacy.ThaiDuongPharmacyAPI.dto.response.RelatedProductResponse(" +
           "p.id, p.name, p.slug, p.imageUrl, p.price, p.isBestSeller) " +
           "FROM Product p " +
           "WHERE p.category.id = :categoryId " +
           "AND p.id != :excludeProductId " +
           "AND p.isActive = true")
    Page<RelatedProductResponse> findRelatedProducts(
            @Param("categoryId") Long categoryId,
            @Param("excludeProductId") Long excludeProductId,
            Pageable pageable);
}

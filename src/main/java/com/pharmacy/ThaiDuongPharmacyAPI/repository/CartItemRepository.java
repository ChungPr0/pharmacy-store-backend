package com.pharmacy.ThaiDuongPharmacyAPI.repository;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    
    List<CartItem> findByCartIdAndProductIdIn(Long cartId, List<Long> productIds);
}

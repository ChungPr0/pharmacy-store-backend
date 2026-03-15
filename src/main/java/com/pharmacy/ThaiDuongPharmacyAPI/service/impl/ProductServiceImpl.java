package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.PagedResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.ProductCardResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Category;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Product;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CategoryRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final int MIN_EXPIRY_DAYS_REMAINING = 90;

    @Override
    public PagedResponse<ProductCardResponse> getValidBestSellers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        LocalDate minExpiryDate = LocalDate.now().plusDays(MIN_EXPIRY_DAYS_REMAINING);
        Page<Product> productPage = productRepository.findValidBestSellers(minExpiryDate, pageable);
        return mapToPagedResponse(productPage);
    }

    @Override
    public PagedResponse<ProductCardResponse> getValidLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        LocalDate minExpiryDate = LocalDate.now().plusDays(MIN_EXPIRY_DAYS_REMAINING);
        Page<Product> productPage = productRepository.findValidLatestProducts(minExpiryDate, pageable);
        return mapToPagedResponse(productPage);
    }

    @Override
    public PagedResponse<ProductCardResponse> getValidProductsByCategory(Long categoryId, int page, int size) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new PagedResponse<>(List.of(), page, size, 0, 0, true);
        }

        List<Long> categoryIds = new ArrayList<>();
        collectCategoryIds(category, categoryIds);

        Pageable pageable = PageRequest.of(page, size);
        LocalDate minExpiryDate = LocalDate.now().plusDays(MIN_EXPIRY_DAYS_REMAINING);
        Page<Product> productPage = productRepository.findValidProductsByCategoryIds(categoryIds, minExpiryDate, pageable);
        return mapToPagedResponse(productPage);
    }

    private ProductCardResponse mapToCardResponse(Product product) {
        return new ProductCardResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getImageUrl(),
                product.getPrice(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getIsBestSeller() != null && product.getIsBestSeller()
        );
    }

    private PagedResponse<ProductCardResponse> mapToPagedResponse(Page<Product> productPage) {
        List<ProductCardResponse> content = productPage.getContent().stream()
                .map(this::mapToCardResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    private void collectCategoryIds(Category category, List<Long> ids) {
        if (category == null) return;
        ids.add(category.getId());
        if (category.getChildren() != null) {
            for (Category child : category.getChildren()) {
                collectCategoryIds(child, ids);
            }
        }
    }
}

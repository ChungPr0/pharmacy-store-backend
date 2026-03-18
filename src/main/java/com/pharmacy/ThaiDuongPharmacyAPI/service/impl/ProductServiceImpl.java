package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.request.ProductSearchRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.*;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Category;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Product;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.ProductImage;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ResourceNotFoundException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CategoryRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final int MIN_EXPIRY_DAYS_REMAINING = 90;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductCardResponse> getValidBestSellers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        LocalDate minExpiryDate = getMinExpiryDate();
        Page<Product> productPage = productRepository.findValidBestSellers(minExpiryDate, pageable);
        return mapToPagedResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductCardResponse> getValidLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        LocalDate minExpiryDate = getMinExpiryDate();
        Page<Product> productPage = productRepository.findValidLatestProducts(minExpiryDate, pageable);
        return mapToPagedResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductSearchResponse> searchProducts(ProductSearchRequestDTO request) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDir()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize(), sort);

        Page<ProductSearchResponse> productPage;

        if (request.getCategorySlug() == null || request.getCategorySlug().trim().isEmpty()) {
            productPage = productRepository.searchProductsGloballyByKeyword(
                    request.getKeyword(),
                    pageable
            );
        } else {
            Category category = categoryRepository.findBySlug(request.getCategorySlug())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với slug: " + request.getCategorySlug()));

            List<Long> categoryIds = new ArrayList<>();
            collectCategoryIds(category, categoryIds);

            productPage = productRepository.searchProductsByCategoryIdsAndKeyword(
                    categoryIds,
                    request.getKeyword(),
                    pageable
            );
        }

        return new PagedResponse<>(
                productPage.getContent(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(String slug) {
        Product product = productRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        Product productWithAttributes = productRepository.findProductAttributesById(product.getId())
                .orElse(product);

        Long totalStockQuantity = productRepository.getTotalStockQuantityByProductId(product.getId());

        List<String> imageUrls = Optional.ofNullable(product.getImages())
                .orElse(Collections.emptyList())
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        List<ProductAttributeDTO> attributes = Optional.ofNullable(productWithAttributes.getAttributes())
                .orElse(Collections.emptyList())
                .stream()
                .map(attr -> new ProductAttributeDTO(attr.getAttributeName(), attr.getAttributeValue()))
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getPrice(),
                totalStockQuantity,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getCategory() != null ? product.getCategory().getSlug() : null,
                imageUrls,
                attributes,
                product.getDescription(),
                product.getIsBestSeller()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelatedProductResponse> getRelatedProducts(String slug, int limit) {
        Product product = productRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        int safeLimit = Math.min(limit, 10);
        Pageable pageable = PageRequest.of(0, safeLimit);

        Page<RelatedProductResponse> relatedProductsPage = productRepository.findRelatedProducts(
                product.getCategory().getId(),
                product.getId(),
                pageable
        );

        return relatedProductsPage.getContent();
    }

    private LocalDate getMinExpiryDate() {
        return LocalDate.now().plusDays(MIN_EXPIRY_DAYS_REMAINING);
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
                .toList();

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

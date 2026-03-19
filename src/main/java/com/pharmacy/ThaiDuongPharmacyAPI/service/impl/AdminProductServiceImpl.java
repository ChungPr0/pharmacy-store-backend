package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request.AdminProductRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.AdminProductDetailResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.AdminProductListResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.PagedResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Category;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Product;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.ProductAttribute;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.ProductImage;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.BadRequestException;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ResourceNotFoundException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CategoryRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AdminProductService;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AdminProductListResponse> getProductList(int pageNo, int pageSize, String keyword, String categorySlug) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminProductListResponse> page = productRepository.getAdminProductList(keyword, categorySlug, pageable);
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    @Transactional
    public Map<String, Object> createProduct(AdminProductRequest request) {
        String slug = SlugUtils.toSlug(request.getName());
        if (productRepository.existsBySlug(slug)) {
            throw new BadRequestException("Tên sản phẩm đã tồn tại!");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(slug);
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setIsActive(request.getIsActive());
        product.setCategory(category);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            product.setImageUrl(request.getImages().get(0)); // First image is thumbnail
            for (String url : request.getImages()) {
                ProductImage image = new ProductImage();
                image.setImageUrl(url);
                image.setProduct(product);
                product.getImages().add(image);
            }
        }

        if (request.getAttributes() != null) {
            for (AdminProductRequest.ProductAttributeRequestDTO attrDto : request.getAttributes()) {
                ProductAttribute attr = new ProductAttribute();
                attr.setAttributeName(attrDto.getName());
                attr.setAttributeValue(attrDto.getValue());
                attr.setProduct(product);
                product.getAttributes().add(attr);
            }
        }

        try {
            Product savedProduct = productRepository.save(product);
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedProduct.getId());
            response.put("slug", savedProduct.getSlug());
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Tên sản phẩm đã tồn tại!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AdminProductDetailResponse getProductDetail(String slug) {
        Product product = productRepository.findAdminBySlugWithDetails(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        product.getAttributes().size(); 
        
        return mapToDetailResponse(product);
    }

    @Override
    @Transactional
    public Map<String, String> updateProduct(String slug, AdminProductRequest request) {
        Product product = productRepository.findAdminBySlugWithDetails(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        product.getAttributes().size();

        String newSlug = SlugUtils.toSlug(request.getName());
        if (!newSlug.equals(product.getSlug()) && productRepository.existsBySlug(newSlug)) {
            throw new BadRequestException("Tên sản phẩm đã tồn tại!");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        product.setName(request.getName());
        product.setSlug(newSlug);
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setIsActive(request.getIsActive());
        product.setCategory(category);

        product.getImages().clear();
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            product.setImageUrl(request.getImages().get(0));
            for (String url : request.getImages()) {
                ProductImage image = new ProductImage();
                image.setImageUrl(url);
                image.setProduct(product);
                product.getImages().add(image);
            }
        } else {
            product.setImageUrl(null);
        }

        product.getAttributes().clear();
        if (request.getAttributes() != null) {
            for (AdminProductRequest.ProductAttributeRequestDTO attrDto : request.getAttributes()) {
                ProductAttribute attr = new ProductAttribute();
                attr.setAttributeName(attrDto.getName());
                attr.setAttributeValue(attrDto.getValue());
                attr.setProduct(product);
                product.getAttributes().add(attr);
            }
        }

        try {
            productRepository.save(product);
            Map<String, String> response = new HashMap<>();
            response.put("slug", newSlug);
            return response;
        } catch (DataIntegrityViolationException e) {
             throw new BadRequestException("Tên sản phẩm đã tồn tại!");
        }
    }

    @Override
    @Transactional
    public void toggleProductStatus(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        product.setIsActive(!product.getIsActive());
        productRepository.save(product);
    }

    private AdminProductDetailResponse mapToDetailResponse(Product product) {
        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        List<AdminProductDetailResponse.ProductAttributeResponse> attributes = product.getAttributes().stream()
                .map(attr -> new AdminProductDetailResponse.ProductAttributeResponse(attr.getAttributeName(), attr.getAttributeValue()))
                .collect(Collectors.toList());

        return AdminProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .price(product.getPrice())
                .description(product.getDescription())
                .isActive(product.getIsActive())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .images(imageUrls)
                .attributes(attributes)
                .build();
    }
}

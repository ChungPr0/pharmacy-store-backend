package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.request.CategoryRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.CategoryHierarchyResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.CategoryTreeResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.SubCategoryResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Category;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.BadRequestException;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ResourceNotFoundException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CategoryRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CategoryService;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getCategoryTree() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::mapToTreeResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryHierarchyResponse getCategoryHierarchy(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với slug: " + slug));

        List<SubCategoryResponse> children = (category.getChildren() == null || category.getChildren().isEmpty())
                ? Collections.emptyList()
                : category.getChildren().stream()
                .map(child -> new SubCategoryResponse(child.getId(), child.getName(), child.getSlug()))
                .toList();

        return new CategoryHierarchyResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                children
        );
    }

    @Override
    @Transactional
    public CategoryTreeResponse createCategory(CategoryRequestDTO request) {
        String slug = SlugUtils.toSlug(request.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Tên danh mục đã tồn tại!");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(slug);

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha"));
            category.setParent(parent);
        }

        try {
            Category savedCategory = categoryRepository.save(category);
            return mapToTreeResponse(savedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Tên danh mục đã tồn tại!");
        }
    }

    @Override
    @Transactional
    public CategoryTreeResponse updateCategory(String slug, CategoryRequestDTO request) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        String newSlug = SlugUtils.toSlug(request.getName());

        if (!newSlug.equals(category.getSlug()) && categoryRepository.existsBySlug(newSlug)) {
            throw new BadRequestException("Tên danh mục đã tồn tại!");
        }

        category.setName(request.getName());
        category.setSlug(newSlug);

        if (request.getParentId() != null) {
            if (category.getId().equals(request.getParentId())) {
                throw new ApiException(400, "Danh mục không thể tự làm cha của chính nó");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        try {
            Category updatedCategory = categoryRepository.save(category);
            return mapToTreeResponse(updatedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Tên danh mục đã tồn tại!");
        }
    }

    @Override
    @Transactional
    public void deleteCategory(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            throw new ApiException(400, "Không thể xóa: Vui lòng xóa các danh mục con trước");
        }

        if (productRepository.existsByCategoryId(category.getId())) {
            throw new ApiException(400, "Không thể xóa: Danh mục đang chứa sản phẩm");
        }

        categoryRepository.delete(category);
    }

    private CategoryTreeResponse mapToTreeResponse(Category category) {
        List<CategoryTreeResponse> childrenResponse = (category.getChildren() == null || category.getChildren().isEmpty())
                ? Collections.emptyList()
                : category.getChildren().stream()
                .map(this::mapToTreeResponse)
                .toList();

        return new CategoryTreeResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                childrenResponse
        );
    }
}

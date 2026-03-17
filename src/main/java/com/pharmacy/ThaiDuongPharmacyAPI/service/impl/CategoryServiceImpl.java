package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.CategoryHierarchyResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.CategoryTreeResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.response.SubCategoryResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Category;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ResourceNotFoundException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CategoryRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

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

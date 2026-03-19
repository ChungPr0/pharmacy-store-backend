package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.request.CategoryRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.CategoryHierarchyResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.CategoryTreeResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryTreeResponse> getCategoryTree();
    CategoryHierarchyResponse getCategoryHierarchy(String slug);
    CategoryTreeResponse createCategory(CategoryRequestDTO request);
    CategoryTreeResponse updateCategory(String slug, CategoryRequestDTO request);
    void deleteCategory(String slug);
}

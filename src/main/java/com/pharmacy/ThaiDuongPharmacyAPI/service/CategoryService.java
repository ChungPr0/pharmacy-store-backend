package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.request.CategoryRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.CategoryHierarchyResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.category.response.CategoryTreeResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryTreeResponse> getCategoryTree();
    CategoryHierarchyResponse getCategoryHierarchy(String slug);
    CategoryTreeResponse createCategory(CategoryRequest request);
    CategoryTreeResponse updateCategory(String slug, CategoryRequest request);
    void deleteCategory(String slug);
}

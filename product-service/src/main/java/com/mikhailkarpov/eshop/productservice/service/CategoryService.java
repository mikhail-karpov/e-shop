package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse createSubcategory(Long parentId, CategoryRequest request);

    void delete(Long id);

    void delete(Long id, boolean forced);

    CategoryResponse findById(Long id);

    List<CategoryResponse> findParentCategories();

    List<CategoryResponse> findSubcategoriesByParentId(Long id);

    CategoryResponse update(Long id, CategoryRequest update);

    void addProduct(Long id, String productCode);

    void removeProduct(Long id, String productCode);

}

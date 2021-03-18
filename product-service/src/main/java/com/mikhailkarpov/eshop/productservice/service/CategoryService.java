package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;

import java.util.List;

public interface CategoryService {

    Category create(CategoryRequest request);

    Category createSubcategory(Long parentId, CategoryRequest request);

    void delete(Long id);

    Category findById(Long id);

    List<Category> findParentCategories();

    List<Category> findSubcategoriesByParentId(Long id);

    Category update(Long id, CategoryRequest update);

    void addProduct(Long id, String productCode);

    void removeProduct(Long id, String productCode);
}

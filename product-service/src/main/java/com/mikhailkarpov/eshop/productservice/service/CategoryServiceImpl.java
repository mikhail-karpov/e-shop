package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.exception.ResourceNotFoundException;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.CategoryRepository;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public Category create(CategoryRequest request) {

        Category category = new Category();
        category.setTitle(request.getTitle());
        category.setDescription(request.getDescription());

        category = categoryRepository.save(category);
        log.info("Creating {}", category);

        return category;
    }

    @Override
    public Category createSubcategory(Long parentId, CategoryRequest request) {

        Category parent = findById(parentId);
        Category subcategory = parent.createSubcategory(request.getTitle(), request.getDescription());

        subcategory = categoryRepository.save(subcategory);
        log.info("Creating {} with parent-id={}", subcategory, parentId);

        return subcategory;
    }

    @Override
    public void delete(Long id) {

        //todo check subcategories and products
        Category category = findById(id);
    }

    @Override
    public Category findById(Long id) {

        return categoryRepository.findById(id).orElseThrow(() -> {
            String message = String.format("Category with id=%d not found", id);
            return new ResourceNotFoundException(message);
        });
    }

    @Override
    public List<Category> findParentCategories() {

        List<Category> categories = new ArrayList<>();
        categoryRepository.findAllByParentId(null).forEach(categories::add);
        return categories;
    }

    @Override
    public List<Category> findSubcategoriesByParentId(Long id) {

        List<Category> subcategories = new ArrayList<>();
        categoryRepository.findAllByParentId(id).forEach(subcategories::add);

        if (subcategories.isEmpty() && !categoryRepository.findById(id).isPresent()) {
            String message = String.format("Category with id=%d not found", id);
            throw  new ResourceNotFoundException(message);
        }

        return subcategories;
    }

    @Override
    public Category update(Long id, CategoryRequest update) {

        Category category = findById(id);
        category.setTitle(update.getTitle());
        category.setDescription(update.getDescription());

        log.info("Updating {}", category);
        return category;
    }

    @Override
    public void addProduct(Long id, String productCode) {

        Category category = findById(id);
        Product product = findProductByCode(productCode);

        category.addProduct(product);
        log.info("Added {} to {}", product, category);
    }

    @Override
    public void removeProduct(Long id, String productCode) {

        Category category = findById(id);
        Product product = findProductByCode(productCode);

        category.removeProduct(product);
        log.info("Removed {} from {}", product, category);
    }

    private Product findProductByCode(String productCode) {
        return productRepository.findById(productCode).orElseThrow(() -> {
            String message = String.format("Product with code=%s not found", productCode);
            return new ResourceNotFoundException(message);
        });
    }
}

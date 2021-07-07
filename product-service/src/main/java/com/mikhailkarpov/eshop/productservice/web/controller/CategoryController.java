package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.service.CategoryService;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> findParentCategories() {

        return categoryService.findParentCategories()
                .stream()
                .sorted(Comparator.comparing(CategoryResponse::getTitle))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable Long id) {

        return categoryService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request,
                                           UriComponentsBuilder uriComponentsBuilder) {

        CategoryResponse category = categoryService.createCategory(request);
        URI location = uriComponentsBuilder.path("/categories/{id}").build(category.getId());

        return ResponseEntity.created(location).body(category);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest update) {

        return categoryService.update(id, update);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestParam(required = false, name = "forced", defaultValue = "false") Boolean forced) {

        categoryService.delete(id, forced);
    }

    @GetMapping("/{id}/subcategories")
    public List<CategoryResponse> findSubcategoriesByParentId(@PathVariable("id") Long id) {

        return categoryService.findSubcategoriesByParentId(id)
                .stream()
                .sorted(Comparator.comparing(CategoryResponse::getTitle))
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/subcategories")
    public ResponseEntity<CategoryResponse> createSubcategory(@PathVariable("id") Long parentId,
                                                      @Valid @RequestBody CategoryRequest request,
                                                      UriComponentsBuilder uriComponentsBuilder) {

        CategoryResponse subcategory = categoryService.createSubcategory(parentId, request);
        URI location = uriComponentsBuilder.path("/categories/{id}").build(subcategory.getId());

        return ResponseEntity.created(location).body(subcategory);
    }

    @PostMapping("/{id}/products")
    public void addProduct(@PathVariable Long id, @RequestParam("code") String productCode) {

        categoryService.addProduct(id, productCode);
    }

    @DeleteMapping("/{id}/products")
    public void removeProduct(@PathVariable Long id, @RequestParam("code") String productCode) {

        categoryService.removeProduct(id, productCode);
    }
}

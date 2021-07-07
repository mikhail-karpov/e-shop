package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.specification.ProductSpecification;
import com.mikhailkarpov.eshop.productservice.service.ProductService;
import com.mikhailkarpov.eshop.productservice.web.dto.PagedResult;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public PagedResult<Product> findProducts(@RequestParam(value = "title") Optional<String> title,
                                             @RequestParam(value = "category") Optional<Long> categoryId,
                                             Pageable pageable) {

        Specification<Product> productSpec = buildSpecification(title, categoryId);
        Page<Product> products = productService.findAll(productSpec, pageable);

        return new PagedResult<>(products);
    }

    @GetMapping("/{code}")
    public Product findProductByCode(@PathVariable String code) {

        return productService.findByCode(code);
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest request,
                                          UriComponentsBuilder uriComponentsBuilder) {

        Product product = productService.create(request);
        URI location = uriComponentsBuilder.path("/products/{code}").build(product.getCode());

        return ResponseEntity.created(location).body(product);
    }

    @PutMapping("/{code}")
    public Product update(@PathVariable String code, @Valid @RequestBody ProductRequest update) {

        return productService.update(code, update);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {

        productService.delete(code);
    }

    private Specification<Product> buildSpecification(Optional<String> title, Optional<Long> categoryId) {
        Specification<Product> productSpec = ProductSpecification.titleLike("%");

        if (title.isPresent()) {
            productSpec = ProductSpecification.titleLike(title.get());
        }
        if (categoryId.isPresent()) {
            productSpec = productSpec.and(ProductSpecification.categoryIdEqual(categoryId.get()));
        }
        return productSpec;
    }
}

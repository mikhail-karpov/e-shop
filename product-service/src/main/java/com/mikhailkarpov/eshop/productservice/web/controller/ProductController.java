package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.service.ProductService;
import com.mikhailkarpov.eshop.productservice.web.dto.PagedResult;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public PagedResult<Product> findProducts(@RequestParam(value = "name") String name,
                                             @RequestParam(value = "code") List<String> codes,
                                             @RequestParam(value = "category") Long categoryId,
                                             @RequestParam(value = "page") Optional<Integer> page,
                                             @RequestParam(value = "limit") Optional<Integer> limit) {

        Pageable pageable = buildPageable(page, limit);
        Page<Product> products = productService.findAll(pageable);

        return new PagedResult<>(products);
    }

    @GetMapping("/{code")
    public Product findProductByCode(@PathVariable String code) {

        return productService.findByCode(code);
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest request, UriComponentsBuilder uriComponentsBuilder) {

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

    private Pageable buildPageable(Optional<Integer> page, Optional<Integer> limit) {
        return null;
    }
}

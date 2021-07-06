package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.exception.ProductDuplicateCodeException;
import com.mikhailkarpov.eshop.productservice.exception.ResourceNotFoundException;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product create(ProductRequest request) {

        String code = request.getCode();
        if (productRepository.existsById(code)) {
            String message = String.format("Product with code=\"%s\" already exists", code);
            throw new ProductDuplicateCodeException(message);
        }

        Product product = Product.builder()
                .code(code)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();

        product = productRepository.save(product);
        log.info("Saving {}", product);

        return product;
    }

    @Override
    public void delete(String code) {

        Product product = findByCode(code);
        productRepository.delete(product);
        log.info("Deleting {}", product);
    }

    @Override
    public Page<Product> findAll(Specification<Product> specification, Pageable pageable) {

        return productRepository.findAll(specification, pageable);
    }

    @Override
    public Product findByCode(String code) {

        return productRepository.findById(code).orElseThrow(() -> {
            String message = String.format("Product with code=%s not found", code);
            return new ResourceNotFoundException(message);
        });
    }

    @Override
    public Product update(String code, ProductRequest update) {

        Product product = findByCode(code);
        product.setTitle(update.getTitle());
        product.setDescription(update.getDescription());
        product.setPrice(update.getPrice());
        product.setQuantity(update.getQuantity());

        log.info("Updating {}", product);

        return product;
    }
}

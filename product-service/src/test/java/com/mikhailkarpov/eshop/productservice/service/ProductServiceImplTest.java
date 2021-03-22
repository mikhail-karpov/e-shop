package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.exception.ResourceNotFoundException;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import com.mikhailkarpov.eshop.productservice.persistence.specification.ProductSpecification;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    private final Product expectedProduct =
            new Product("abc", "product", "description", 1050, 16);

    @BeforeEach
    void setup() {
        this.productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void givenRequest_whenCreateProduct_thenSaved() {

        ProductRequest request =
                new ProductRequest("abc", "product", "description", 1050, 16);
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(expectedProduct);

        Product created = productService.create(request);

        assertNotNull(created);
        assertThat(created).usingRecursiveComparison().isEqualTo(expectedProduct);

        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductExists_whenDelete_thenDeleted() {

        when(productRepository.findById("abc")).thenReturn(Optional.of(expectedProduct));

        productService.delete("abc");

        verify(productRepository).findById("abc");
        verify(productRepository).delete(any());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductExists_whenDelete_thenThrows() {

        when(productRepository.findById("abc")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.delete("abc"));

        verify(productRepository).findById("abc");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProducts_whenFindAll_thenFound() {

        List<Product> productList = Arrays.asList(expectedProduct);
        Page<Product> expectedPage = new PageImpl<>(productList);

        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<Product> foundPage =
                productService.findAll(ProductSpecification.titleLike("product"), PageRequest.of(11, 12));

        assertThat(expectedPage).usingRecursiveComparison().isEqualTo(foundPage);
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductExists_whenFindByCode_thenFound() {

        when(productRepository.findById("abc")).thenReturn(Optional.of(expectedProduct));

        Product foundProduct = productService.findByCode("abc");

        assertThat(expectedProduct).usingRecursiveComparison().isEqualTo(foundProduct);
        verify(productRepository).findById("abc");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductNotExists_whenFindByCode_thenThrows() {

        when(productRepository.findById("abc")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findByCode("abc"));
        verify(productRepository).findById("abc");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductExists_whenUpdate_thenOk() {

        when(productRepository.findById("abc")).thenReturn(Optional.of(expectedProduct));

        ProductRequest request =
                new ProductRequest("abc", "update", "update description", 100, 6);
        Product updatedProduct = productService.update("abc", request);

        assertThat(expectedProduct).usingRecursiveComparison().isEqualTo(updatedProduct);
        verify(productRepository).findById("abc");
        verifyNoMoreInteractions(productRepository);
    }
}
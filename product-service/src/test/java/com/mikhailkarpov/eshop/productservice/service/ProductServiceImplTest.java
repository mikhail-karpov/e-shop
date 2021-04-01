package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.exception.DuplicateProductCodeException;
import com.mikhailkarpov.eshop.productservice.exception.ResourceNotFoundException;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import com.mikhailkarpov.eshop.productservice.persistence.specification.ProductSpecification;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
        //given
        ProductRequest request =
                new ProductRequest("abc", "product", "description", 1050, 16);
        when(productRepository.existsById("abc")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(expectedProduct);

        //when
        Product created = productService.create(request);

        //then
        assertNotNull(created);
        assertThat(created).usingRecursiveComparison().isEqualTo(expectedProduct);

        verify(productRepository).existsById("abc");
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductWithCodeExists_whenCreateDuplicate_thenThrows() {
        //given
        when(productRepository.existsById("abc")).thenReturn(true);
        ProductRequest request =
                new ProductRequest("abc", "product", "description", 1050, 16);

        //when
        assertThrows(DuplicateProductCodeException.class, () -> productService.create(request));

        //then
        verify(productRepository).existsById("abc");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductExists_whenDelete_thenDeleted() {
        //given
        when(productRepository.findById("abc")).thenReturn(Optional.of(expectedProduct));

        //when
        productService.delete("abc");

        //then
        verify(productRepository).findById("abc");
        verify(productRepository).delete(any());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductDoesNotExist_whenDelete_thenThrows() {
        //given
        when(productRepository.findById("abc")).thenReturn(Optional.empty());

        //when
        assertThrows(ResourceNotFoundException.class, () -> productService.delete("abc"));

        //then
        verify(productRepository).findById("abc");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProducts_whenFindAll_thenFound() {
        //given
        List<Product> productList = Arrays.asList(expectedProduct);
        Page<Product> expectedPage = new PageImpl<>(productList);

        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        //when
        Page<Product> foundPage =
                productService.findAll(ProductSpecification.titleLike("product"), PageRequest.of(11, 12));

        //then
        assertThat(expectedPage).usingRecursiveComparison().isEqualTo(foundPage);
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductExists_whenFindByCode_thenFound() {
        //given
        when(productRepository.findById("abc")).thenReturn(Optional.of(expectedProduct));

        //when
        Product foundProduct = productService.findByCode("abc");

        //then
        assertThat(expectedProduct).usingRecursiveComparison().isEqualTo(foundProduct);
        verify(productRepository).findById("abc");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductNotExists_whenFindByCode_thenThrows() {
        //given
        when(productRepository.findById("abc")).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> productService.findByCode("abc"));
        verify(productRepository).findById("abc");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenProductExists_whenUpdate_thenOk() {
        //given
        when(productRepository.findById("abc")).thenReturn(Optional.of(expectedProduct));

        //when
        ProductRequest request =
                new ProductRequest("abc", "update", "update description", 100, 6);
        Product updatedProduct = productService.update("abc", request);

        //then
        assertThat(expectedProduct).usingRecursiveComparison().isEqualTo(updatedProduct);
        verify(productRepository).findById("abc");
        verifyNoMoreInteractions(productRepository);
    }
}
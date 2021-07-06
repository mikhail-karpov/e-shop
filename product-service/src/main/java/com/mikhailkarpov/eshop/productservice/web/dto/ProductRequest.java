package com.mikhailkarpov.eshop.productservice.web.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for Jackson
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "code is required")
    private String code;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description must be provided")
    private String description;

    @NotNull(message = "price must be provided")
    @Min(value = 1, message = "price must be positive")
    private Integer price;

    @NotNull(message = "quantity must be provided")
    @Min(value = 0, message = "quantity must be non-negative")
    private Integer quantity;

}
package com.mikhailkarpov.eshop.productservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "title must be provided")
    private String title;

    @NotBlank(message = "description must be provided")
    private String description;
}

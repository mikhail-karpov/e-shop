package com.mikhailkarpov.eshop.shoppingcartservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private static final long serialVersionUID = 4093547165038359257L;

    private String code;

    private String title;

    private Integer price;

    private Integer quantity;
}

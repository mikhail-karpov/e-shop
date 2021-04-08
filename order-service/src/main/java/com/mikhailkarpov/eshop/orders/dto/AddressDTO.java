package com.mikhailkarpov.eshop.orders.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class AddressDTO {

    private UUID id;

    @NotBlank(message = "Zip code must be provided")
    private String zip;

    @NotBlank(message = "Country must be provided")
    private String country;

    @NotBlank(message = "State/region must be provided")
    private String state;

    @NotBlank(message = "City must be provided")
    private String city;

    @NotBlank(message = "Street address must be provided")
    private String street;

    @NotBlank(message = "Phone number must be provided")
    private String phone;
}

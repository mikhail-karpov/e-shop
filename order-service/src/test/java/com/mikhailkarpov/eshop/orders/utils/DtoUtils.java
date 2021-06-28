package com.mikhailkarpov.eshop.orders.utils;

import com.mikhailkarpov.eshop.orders.dto.AddressDTO;

public class DtoUtils {

    public static AddressDTO getValidAddress() {
        return AddressDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .zip("zip")
                .country("Country")
                .city("City")
                .street("Street")
                .phone("Phone")
                .build();
    }
}

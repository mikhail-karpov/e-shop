package com.mikhailkarpov.eshop.orders.utils;

import com.mikhailkarpov.eshop.orders.dto.AddressDTO;
import com.mikhailkarpov.eshop.orders.persistence.entities.Address;

public class PojoUtils {

    public static AddressDTO getValidAddressDTO() {
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

    public static Address getValidAddress() {
        return Address.builder()
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

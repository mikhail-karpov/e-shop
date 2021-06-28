package com.mikhailkarpov.eshop.orders.dto;

import com.mikhailkarpov.eshop.orders.persistence.entities.Address;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class AddressDTO {

    @NotBlank(message = "first name must be provided")
    private String firstName;

    @NotBlank(message = "last name must be provided")
    private String lastName;

    @NotBlank(message = "zip code must be provided")
    private String zip;

    @NotBlank(message = "country must be provided")
    private String country;

    @NotBlank(message = "city must be provided")
    private String city;

    @NotBlank(message = "street must be provided")
    private String street;

    @NotBlank(message = "phone number must be provided")
    private String phone;

    @Builder
    public AddressDTO(String lastName, String firstName, String zip, String country, String city, String street, String phone) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.zip = zip;
        this.country = country;
        this.city = city;
        this.street = street;
        this.phone = phone;
    }

    public AddressDTO(Address address) {
        this.lastName = address.getLastName();
        this.firstName = address.getFirstName();
        this.zip = address.getZip();
        this.country = address.getCountry();
        this.city = address.getCity();
        this.street = address.getStreet();
        this.phone = address.getPhone();
    }
}

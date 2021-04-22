package com.mikhailkarpov.eshop.orders.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.UUID;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address {

    @Id
    private UUID id;

    @OneToOne(fetch = LAZY, optional = false)
    @MapsId
    private Order order;

    private String firstName;

    private String lastName;

    private String zip;

    private String country;

    private String city;

    private String street;

    private String phone;

    public Address() {
        super();
    }

    @Override
    public String toString() {
        return "Address{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

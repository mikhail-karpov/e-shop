package com.mikhailkarpov.eshop.orders.entities;

public enum AddressType {

    SHIPPING_ADDRESS("Shipping address"), BILLING_ADDRESS("Billing address");

    private final String title;

    AddressType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

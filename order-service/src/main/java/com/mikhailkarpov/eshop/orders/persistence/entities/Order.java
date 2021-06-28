package com.mikhailkarpov.eshop.orders.persistence.entities;

import lombok.Builder;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Embedded
    private Address shippingAddress;

    @OneToMany(fetch = LAZY, cascade = {PERSIST, MERGE}, orphanRemoval = true, mappedBy = "order")
    private Set<OrderItem> items = new HashSet<>();

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    public Order() {
        this.id = UUID.randomUUID();
    }

    @Builder
    public Order(String customerId, Address shippingAddress, OrderStatus status, Set<OrderItem> items) {
        this();
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.status = status;
        items.forEach(this::addItem);
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        this.items.remove(item);
        item.setOrder(null);
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + getId() + '\'' +
                ", customerId='" + customerId + '\'' +
                ", status=" + status +
                '}';
    }
}

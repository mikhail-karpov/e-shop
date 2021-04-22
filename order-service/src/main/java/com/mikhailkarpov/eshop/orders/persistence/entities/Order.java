package com.mikhailkarpov.eshop.orders.persistence.entities;

import com.mikhailkarpov.eshop.orders.dto.OrderStatus;
import lombok.NoArgsConstructor;

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

    private String customerId;

    @OneToOne(fetch = LAZY, cascade = {PERSIST, MERGE}, orphanRemoval = true, mappedBy = "order")
    private Address shippingAddress;

    @OneToMany(fetch = LAZY, cascade = {PERSIST, MERGE}, orphanRemoval = true, mappedBy = "order")
    private final Set<OrderItem> items = new HashSet<>();

    @Enumerated(STRING)
    private OrderStatus status;

    public Order() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
        this.shippingAddress.setOrder(this);
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        this.items.remove(item);
        item.setOrder(null);
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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

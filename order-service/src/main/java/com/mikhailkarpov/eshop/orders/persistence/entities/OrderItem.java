package com.mikhailkarpov.eshop.orders.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.UUID;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "order_item")
@Getter
@Setter
public class OrderItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "order_fk")
    private Order order;

    private String code;

    private Integer quantity;

    public OrderItem() {
        this.id = UUID.randomUUID();
    }

    public OrderItem(String code, Integer quantity) {
        this();
        this.code = code;
        this.quantity = quantity;
    }

    protected void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "code='" + code + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}

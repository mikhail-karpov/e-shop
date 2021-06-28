package com.mikhailkarpov.eshop.orders.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.UUID;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @Getter
    private UUID id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "order_fk", nullable = false, updatable = false)
    @Getter
    private Order order;

    @Getter
    @Setter
    @Column(name = "code", nullable = false)
    private String code;

    @Getter
    @Setter
    @Column(name = "quantity", nullable = false)
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
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}

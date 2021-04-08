package com.mikhailkarpov.eshop.orders.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Data
@NoArgsConstructor
public class OrderStatus {

    @Column(name = "status_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatusType type;

    @Column(name = "status_comment")
    private String comment;

    public OrderStatus(OrderStatusType type, String comment) {
        this.type = type;
        this.comment = comment;
    }
}

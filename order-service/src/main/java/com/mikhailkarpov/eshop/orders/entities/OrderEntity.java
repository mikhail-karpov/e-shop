package com.mikhailkarpov.eshop.orders.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;

@Entity(name = "OrderEntity")
@Table(name = "order")
@NoArgsConstructor
@Getter
@Setter
public class OrderEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @OneToOne(fetch = LAZY, cascade = {PERSIST, MERGE}, mappedBy = "order")
    private AddressEntity shippingAddress;

    @OneToOne(fetch = LAZY, cascade = {PERSIST, MERGE}, mappedBy = "order")
    private AddressEntity billingAddress;

    @OneToMany(fetch = LAZY, cascade = {PERSIST, MERGE}, mappedBy = "order")
    private Set<OrderItemEntity> items = new HashSet<>();

    @Embedded
    private OrderStatus status;

    public void setShippingAddress(AddressEntity shippingAddress) {
        Assert.notNull(shippingAddress, "Address must be provided");
        this.shippingAddress = shippingAddress;
        this.shippingAddress.setOrder(this);
    }

    public void setBillingAddress(AddressEntity billingAddress) {
        Assert.notNull(billingAddress, "Address must be provided");
        this.billingAddress = billingAddress;
        this.billingAddress.setOrder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        OrderEntity that = (OrderEntity) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

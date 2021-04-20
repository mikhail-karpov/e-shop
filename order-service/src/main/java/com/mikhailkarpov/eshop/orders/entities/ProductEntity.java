package com.mikhailkarpov.eshop.orders.entities;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity(name = "Product")
@Table(name = "product")
@NoArgsConstructor
@Getter
@Setter
public class ProductEntity {

    private String code;

    private String title;

    private Integer price;

    private Integer quantity;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "order_fk", nullable = false, updatable = false)
    private OrderEntity order;

    @Builder
    protected ProductEntity(String code, String title, Integer price, Integer quantity) {
        this.code = code;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProductEntity that = (ProductEntity) o;

        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "OrderItemEntity{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}

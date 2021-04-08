package com.mikhailkarpov.eshop.orders.entities;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "Address")
@Table(name = "address")
@NoArgsConstructor
@Getter
@Setter
public class AddressEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_fk", nullable = false, updatable = false)
    private OrderEntity order;

    @Column(name = "zip", nullable = false)
    private String zip;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "phone_number", nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AddressType type;

    void setOrder(OrderEntity order) {
        Assert.notNull(order, "Order must be provided");
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AddressEntity that = (AddressEntity) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "id=" + id +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", streetAddress='" + street + '\'' +
                ", phoneNumber='" + phone + '\'' +
                ", type=" + type +
                '}';
    }
}

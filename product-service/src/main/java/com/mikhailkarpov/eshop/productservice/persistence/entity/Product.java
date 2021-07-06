package com.mikhailkarpov.eshop.productservice.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mikhailkarpov.eshop.productservice.exception.ProductReservationException;
import lombok.*;

import javax.persistence.*;

import java.io.Serializable;

import static javax.persistence.FetchType.LAZY;

@Entity(name = "Product")
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for JPA
public class Product implements Serializable {

    private static final long serialVersionUID = 535196700385883110L;

    @Id
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reserved", nullable = false)
    @JsonIgnore
    private Integer reserved;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_fk")
    @JsonIgnore
    private Category category;

    @Builder
    public Product(String code, String title, String description, int price, int quantity) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.reserved = 0;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReserved() {
        return reserved;
    }

    public void addReserved(int reserved) throws ProductReservationException {

        if (reserved < 0) {
            String message = "Non-negative amount must be provided";
            throw new ProductReservationException(message);
        }

        int total = getQuantity();
        int alreadyReserved = getReserved();

        if (reserved > total - alreadyReserved) {
            String message = "Not enough product for reservation";
            throw new ProductReservationException(message);
        }

        this.reserved = reserved + alreadyReserved;
    }

    protected Category getCategory() {
        return category;
    }

    protected void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Product product = (Product) o;

        return code.equals(product.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "Product{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", reserved=" + reserved +
                '}';
    }
}

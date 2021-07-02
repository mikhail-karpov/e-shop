package com.mikhailkarpov.eshop.productservice.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mikhailkarpov.eshop.productservice.exception.ProductNotValidException;
import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;

import java.io.Serializable;

import static javax.persistence.FetchType.LAZY;

@Entity(name = "Product")
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for JPA
@Getter
@Setter
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
    private Integer reserved;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_fk")
    @JsonIgnore
    private Category category;

    @Builder
    public Product(String code, String title, String description, Integer price, Integer quantity) {
        setCode(code);
        setTitle(title);
        setDescription(description);
        setPrice(price);
        setQuantity(quantity);
        setReserved(0);
    }

    public void setCode(String code) {
        if (code == null || code.isEmpty()) {
            throw new ProductNotValidException("Code must be provided");
        }
        this.code = code;
    }

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new ProductNotValidException("Title must be provided");
        }
        this.title = title;
    }

    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new ProductNotValidException("Title must be provided");
        }this.description = description;
    }

    public void setPrice(Integer price) {
        if (price == null || price <= 0) {
            throw new ProductNotValidException("Invalid price: " + price);
        }
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new ProductNotValidException("Invalid quantity: " + quantity);
        }
        this.quantity = quantity;
    }

    public void setReserved(Integer reserved) {
        if (reserved == null || reserved < 0) {
            throw new ProductNotValidException("Invalid price: " + price);
        } else if (reserved > quantity) {
            String message = String.format("Reserved = %d > total quantity = %d", reserved, quantity);
            throw new ProductNotValidException(message);
        }
        this.reserved = reserved;
    }

    protected void setCategory(Category category) {
        if (category == null) {
            throw new ProductNotValidException("Category must be provided");
        }
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

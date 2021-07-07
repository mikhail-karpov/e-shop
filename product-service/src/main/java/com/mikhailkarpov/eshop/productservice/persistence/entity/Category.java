package com.mikhailkarpov.eshop.productservice.persistence.entity;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Entity(name = "Category")
@Table(name = "category")
@NoArgsConstructor
public class Category implements Serializable {

    private static final long serialVersionUID = -980204763778840631L;

    @Id
    @SequenceGenerator(name = "category_id_seq", sequenceName = "category_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_seq")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(fetch = LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, mappedBy = "parent")
    private final Set<Category> subcategories = new HashSet<>();

    @OneToMany(fetch = LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, mappedBy = "category")
    private final Set<Product> products = new HashSet<>();

    public Category(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Category createSubcategory(String title, String description) {
        Category category = new Category();
        category.setTitle(title);
        category.setDescription(description);
        category.parent = this;

        this.subcategories.add(category);

        return category;
    }

    public void addProduct(Product product) {
        product.setCategory(this);
        this.products.add(product);
    }

    public void removeProduct(Product product) {
        product.setCategory(null);
        this.products.remove(product);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Category category = (Category) o;

        if (!Objects.equals(id, category.id))
            return false;
        return title.equals(category.title);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + title.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

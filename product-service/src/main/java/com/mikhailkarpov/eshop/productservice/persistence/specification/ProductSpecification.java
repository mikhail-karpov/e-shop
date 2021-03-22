package com.mikhailkarpov.eshop.productservice.persistence.specification;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class ProductSpecification {

    public static Specification<Product> titleLike(String title) {

        return (root, query, criteriaBuilder) -> title == null ?
                criteriaBuilder.conjunction() :
                criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), title.toUpperCase());
    }

    public static Specification<Product> categoryIdEqual(Long categoryId) {

        return (root, query, criteriaBuilder) -> {
            Join<Product, Category> join = root.join("category", JoinType.LEFT);

            return categoryId == null ?
                    criteriaBuilder.conjunction() :
                    criteriaBuilder.equal(join.get("id"), categoryId);
        };
    }
}

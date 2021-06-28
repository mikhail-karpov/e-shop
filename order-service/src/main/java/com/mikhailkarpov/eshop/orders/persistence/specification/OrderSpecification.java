package com.mikhailkarpov.eshop.orders.persistence.specification;

import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.persistence.entities.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public class OrderSpecification {

    public static Specification<Order> byCustomerId(@Nullable String customerId) {
        return (root, query, criteriaBuilder) -> customerId != null ?
                criteriaBuilder.equal(root.get("customerId"), customerId) :
                criteriaBuilder.conjunction();
    }

    public static Specification<Order> byStatus(@Nullable OrderStatus status) {
        return (root, query, criteriaBuilder) -> status != null ?
                criteriaBuilder.equal(root.get("status"), status) :
                criteriaBuilder.conjunction();
    }
}

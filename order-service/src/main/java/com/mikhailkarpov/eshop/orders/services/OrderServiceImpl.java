package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.*;
import com.mikhailkarpov.eshop.orders.exceptions.OrderNotFoundException;
import com.mikhailkarpov.eshop.orders.persistence.entities.Address;
import com.mikhailkarpov.eshop.orders.persistence.entities.Order;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderItem;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.persistence.repositories.OrderItemRepository;
import com.mikhailkarpov.eshop.orders.persistence.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mikhailkarpov.eshop.orders.persistence.specification.OrderSpecification.byCustomerId;
import static com.mikhailkarpov.eshop.orders.persistence.specification.OrderSpecification.byStatus;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public UUID createOrder(String customerId, CreateOrderRequest request) {

        Address shippingAddress = modelMapper.map(request.getShippingAddress(), Address.class);

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setShippingAddress(shippingAddress);
        order.setStatus(OrderStatus.ACCEPTED);

        request.getItems().stream().forEach(item -> {
            OrderItem orderItem = modelMapper.map(item, OrderItem.class);
            order.addItem(orderItem);
        });

        return orderRepository.save(order).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<OrderDTO> searchOrders(SearchOrdersRequest request, Pageable pageable) {

        Specification<Order> spec = byCustomerId(request.getCustomerId()).and(byStatus(request.getStatus()));

        Page<OrderDTO> orderDTOPage = orderRepository.findAll(spec, pageable).map(this::mapFromEntity);

        return new PagedResult<>(orderDTOPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<OrderDTO> findAll(Pageable pageable) {

        Page<OrderDTO> orderDTOPage = orderRepository.findAll(pageable).map(this::mapFromEntity);
        return new PagedResult<>(orderDTOPage);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderWithItemsDTO findOrderById(UUID orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            String message = String.format("Order with id=\"%s\" not found", orderId);
            return new OrderNotFoundException(message);
        });

        List<OrderItemDTO> items = order.getItems().stream()
                .map(OrderItemDTO::new)
                .sorted(Comparator.comparing(OrderItemDTO::getCode))
                .collect(Collectors.toList());
        AddressDTO address = new AddressDTO(order.getShippingAddress());
        return new OrderWithItemsDTO(orderId, order.getCustomerId(), order.getStatus(), address, items);
    }

    @Override
    @Transactional
    public void updateOrderStatus(UUID id, OrderStatus status) {

        Order order = orderRepository.findById(id).orElseThrow(() -> {
            String message = String.format("Order with id=\"%s\" not found", id);
            return new OrderNotFoundException(message);
        });
        order.setStatus(status);
    }

    private OrderDTO mapFromEntity(Order entity) {

        OrderDTO dto = new OrderDTO();

        dto.setId(entity.getId());
        dto.setCustomerId(entity.getCustomerId());
        dto.setStatus(entity.getStatus());
        dto.setShippingAddress(new AddressDTO(entity.getShippingAddress()));

        return dto;
    }
}

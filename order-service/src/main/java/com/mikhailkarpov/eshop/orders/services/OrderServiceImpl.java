package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.*;
import com.mikhailkarpov.eshop.orders.exceptions.OrderNotFoundException;
import com.mikhailkarpov.eshop.orders.messaging.OrderMessagePublisher;
import com.mikhailkarpov.eshop.orders.messaging.events.OrderCreatedMessage;
import com.mikhailkarpov.eshop.orders.persistence.entities.Address;
import com.mikhailkarpov.eshop.orders.persistence.entities.Order;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderItem;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.persistence.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mikhailkarpov.eshop.orders.persistence.specification.OrderSpecification.byCustomerId;
import static com.mikhailkarpov.eshop.orders.persistence.specification.OrderSpecification.byStatus;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMessagePublisher orderMessagePublisher;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public UUID createOrder(String customerId, CreateOrderRequestBody request) {

        List<OrderItemDTO> items = request.getItems();

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setShippingAddress(modelMapper.map(request.getShippingAddress(), Address.class));
        order.setStatus(OrderStatus.ACCEPTED);

        items.forEach(item -> {
            OrderItem orderItem = modelMapper.map(item, OrderItem.class);
            order.addItem(orderItem);
        });

        UUID id = orderRepository.save(order).getId();
        orderMessagePublisher.send(new OrderCreatedMessage(id, items));

        return id;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<OrderDTO> searchOrders(SearchOrdersRequest request, Pageable pageable) {

        Specification<Order> spec = byCustomerId(request.getCustomerId())
                .and(byStatus(request.getStatus()));

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
    public OrderDTO findOrderById(UUID orderId) {

        return orderRepository.findById(orderId).map(this::mapFromEntity)
                .orElseThrow(() -> {
                    String message = String.format("Order with id=\"%s\" not found", orderId);
                    return new OrderNotFoundException(message);
                });
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

        List<OrderItemDTO> itemDTOList = entity.getItems()
                .stream()
                .map(item -> new OrderItemDTO(item.getCode(), item.getQuantity()))
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .shippingAddress(modelMapper.map(entity.getShippingAddress(), AddressDTO.class))
                .status(entity.getStatus())
                .items(itemDTOList)
                .build();
    }
}

package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.config.MessagingConfig;
import com.mikhailkarpov.eshop.orders.dto.*;
import com.mikhailkarpov.eshop.orders.entities.AddressEntity;
import com.mikhailkarpov.eshop.orders.entities.OrderEntity;
import com.mikhailkarpov.eshop.orders.entities.ProductEntity;
import com.mikhailkarpov.eshop.orders.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.events.OrderPlacedEvent;
import com.mikhailkarpov.eshop.orders.exceptions.OrderNotFoundException;
import com.mikhailkarpov.eshop.orders.repositories.OrderEntityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mikhailkarpov.eshop.orders.entities.OrderStatusType.PLACED;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderEntityRepository orderRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void placeOrder(UUID orderId, String customerId, CreateOrderRequest request) {

        OrderEntity orderEntity = createOrderEntity(orderId, customerId, request);
        orderRepository.save(orderEntity);

        ProductReservationRequestMessage message = new ProductReservationRequestMessage(orderId, request.getItems());
        rabbitTemplate.convertAndSend(MessagingConfig.ORDER_EXCHANGE, MessagingConfig.ROUTING_KEY, message);
    }

    @RabbitListener(queues = MessagingConfig.ORDER_QUEUE)
    public void handle(ProductReservationResponseMessage message) {
        message
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {

        List<OrderDTO> dtoList = new ArrayList<>();

        orderRepository.findAll().forEach(entity -> {
            OrderDTO dto = mapFromEntity(entity);
            dtoList.add(dto);
        });

        return dtoList;
    }

    private OrderDTO mapFromEntity(OrderEntity entity) {

        OrderDTO dto = new OrderDTO();

        dto.setId(entity.getId());
        dto.setCustomerId(entity.getCustomerId());
        dto.setStatus(new OrderStatusDTO(entity.getStatus().getType().toString(), entity.getStatus().getComment()));
        dto.setShippingAddress(modelMapper.map(entity.getShippingAddress(), AddressDTO.class));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO findOrderById(UUID orderId) {

        return orderRepository.findById(orderId).map(this::mapFromEntity).orElseThrow(() -> {
                    String message = String.format("Order with id=\"%s\" not found", orderId);
                    return new OrderNotFoundException(message);
                }
        );
    }

    private OrderEntity createOrderEntity(UUID orderId, String customerId, CreateOrderRequest request) {

        AddressEntity shippingAddress = modelMapper.map(request.getShippingAddress(), AddressEntity.class);

        Set<ProductEntity> orderItems = request.getItems().stream().map(item -> {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setCode(item.getCode());
            productEntity.setQuantity(item.getQuantity());
            return productEntity;
        }).collect(Collectors.toSet());

        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setId(orderId);
        orderEntity.setCustomerId(customerId);
        orderEntity.setShippingAddress(shippingAddress);
        orderEntity.setItems(orderItems);
        orderEntity.setStatus(new OrderStatus(PLACED, "Waiting for order reservation"));

        return orderEntity;
    }
}

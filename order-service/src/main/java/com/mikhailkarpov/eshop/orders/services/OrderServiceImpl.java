package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.AddressDTO;
import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequest;
import com.mikhailkarpov.eshop.orders.dto.OrderDTO;
import com.mikhailkarpov.eshop.orders.dto.OrderStatusDTO;
import com.mikhailkarpov.eshop.orders.entities.AddressEntity;
import com.mikhailkarpov.eshop.orders.entities.OrderEntity;
import com.mikhailkarpov.eshop.orders.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.events.OrderPlacedEvent;
import com.mikhailkarpov.eshop.orders.exceptions.OrderNotFoundException;
import com.mikhailkarpov.eshop.orders.repositories.OrderEntityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mikhailkarpov.eshop.orders.entities.AddressType.BILLING_ADDRESS;
import static com.mikhailkarpov.eshop.orders.entities.AddressType.SHIPPING_ADDRESS;
import static com.mikhailkarpov.eshop.orders.entities.OrderStatusType.PLACED;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ApplicationEventPublisher eventPublisher;
    private final OrderEntityRepository orderRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public void placeOrder(UUID orderId, String customerId, CreateOrderRequest request) {

        OrderEntity orderEntity = createOrderEntity(orderId, customerId, request);
        orderRepository.save(orderEntity);

        OrderPlacedEvent event = new OrderPlacedEvent(orderId, customerId, request);
        eventPublisher.publishEvent(event);
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
        dto.setBillingAddress(modelMapper.map(entity.getBillingAddress(), AddressDTO.class));

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
        shippingAddress.setId(UUID.randomUUID());
        shippingAddress.setType(SHIPPING_ADDRESS);

        AddressEntity billingAddress = modelMapper.map(request.getBillingAddress(), AddressEntity.class);
        billingAddress.setId(UUID.randomUUID());
        billingAddress.setType(BILLING_ADDRESS);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setCustomerId(customerId);
        orderEntity.setShippingAddress(shippingAddress);
        orderEntity.setBillingAddress(billingAddress);
        orderEntity.setStatus(new OrderStatus(PLACED, null));

        return orderEntity;
    }
}

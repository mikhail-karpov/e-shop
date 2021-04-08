package com.mikhailkarpov.eshop.orders.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhailkarpov.eshop.orders.dto.AddressDTO;
import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequest;
import com.mikhailkarpov.eshop.orders.entities.AddressEntity;
import com.mikhailkarpov.eshop.orders.entities.AddressType;
import com.mikhailkarpov.eshop.orders.entities.OrderEntity;
import com.mikhailkarpov.eshop.orders.events.OrderCreatedEvent;
import com.mikhailkarpov.eshop.orders.repositories.OrderEntityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

import static com.mikhailkarpov.eshop.orders.entities.AddressType.BILLING_ADDRESS;
import static com.mikhailkarpov.eshop.orders.entities.AddressType.SHIPPING_ADDRESS;

@Service
@RequiredArgsConstructor
public class OrderCommandServiceImpl implements OrderCommandService {

    private final ModelMapper modelMapper = new ModelMapper();

    private final ApplicationEventPublisher eventPublisher;

    private final OrderEntityRepository orderEntityRepository;

    @Override
    public String placeOrder(CreateOrderRequest request) {

        UUID orderId = UUID.randomUUID();

        AddressEntity shippingAddress = modelMapper.map(request.getShippingAddress(), AddressEntity.class);
        shippingAddress.setId(UUID.randomUUID());
        shippingAddress.setType(SHIPPING_ADDRESS);

        AddressEntity billingAddress = modelMapper.map(request.getBillingAddress(), AddressEntity.class);
        billingAddress.setId(UUID.randomUUID());
        billingAddress.setType(BILLING_ADDRESS);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setCustomerId(request.getCustomerId());
        orderEntity.setShippingAddress(shippingAddress);
        orderEntity.setBillingAddress(shippingAddress);

        orderEntityRepository.save(orderEntity);

        return orderId.toString();
    }
}

package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.ProductDTO;
import com.mikhailkarpov.eshop.orders.exceptions.OrderNotFoundException;
import com.mikhailkarpov.eshop.orders.repositories.OrderEntityRepository;
import com.mikhailkarpov.eshop.orders.repositories.OrderItemEntityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final OrderItemEntityRepository itemRepository;

    private final OrderEntityRepository orderRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsByOrderId(UUID orderId) {

        List<ProductDTO> dtoList = new ArrayList<>();

        itemRepository.findAllByOrderId(orderId).forEach(item -> {
            ProductDTO dto = modelMapper.map(item, ProductDTO.class);
            dtoList.add(dto);
        });

        if (dtoList.isEmpty() && !orderRepository.existsById(orderId)) {
            String message = String.format("Order with id=\"%s\" not found", orderId);
            throw new OrderNotFoundException(message);
        }

        return dtoList;
    }
}

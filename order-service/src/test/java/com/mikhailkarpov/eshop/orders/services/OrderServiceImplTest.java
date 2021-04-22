package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.*;
import com.mikhailkarpov.eshop.orders.exceptions.OrderNotFoundException;
import com.mikhailkarpov.eshop.orders.persistence.entities.Order;
import com.mikhailkarpov.eshop.orders.persistence.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderServiceImplTest {

    @Container
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:12-alpine")
                    .withDatabaseName("order_service")
                    .withUsername("order_service")
                    .withPassword("password");

    @DynamicPropertySource
    static void configDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        orderService = new OrderServiceImpl(orderRepository);
    }

    @Test
    void givenRequest_whenCreateOrderAndFindById_thenSavedAndFound() {
        //given
        CreateOrderRequest request = prepareRequest();

        //when
        UUID id = orderService.createOrder("customer", request);
        Order entity = entityManager.find(Order.class, id);
        OrderDTO dto = orderService.findOrderById(id);

        //then
        assertEquals("customer", entity.getCustomerId());
        assertEquals(2, entity.getItems().size());
        assertEquals(OrderStatus.CREATED, entity.getStatus());
        assertEquals(id, entity.getShippingAddress().getId());

        assertEquals(id, dto.getId());
        assertEquals("customer", dto.getCustomerId());
        assertEquals(OrderStatus.CREATED, dto.getStatus());
    }

    @Test
    void givenNoOrder_whenFindById_thenThrows() {

        assertThrows(OrderNotFoundException.class, () -> orderService.findOrderById(UUID.randomUUID()));
    }

    @Test
    void givenOrder_whenUpdateStatus_thenUpdated() {
        //given
        UUID uuid = orderService.createOrder("customer", prepareRequest());

        //when
        orderService.updateOrderStatus(uuid, OrderStatus.SHIPPED);

        //then
        Order order = entityManager.find(Order.class, uuid);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void givenOrdersAndSearchRequest_whenFindOrders_thenFound() {
        //given
        orderService.createOrder("customer", prepareRequest());
        UUID id = orderService.createOrder("customer", prepareRequest());
        orderService.updateOrderStatus(id, OrderStatus.PAYED);

        //and
        SearchOrdersRequest request = new SearchOrdersRequest();
        request.setCustomerId("customer");
        request.setStatus(OrderStatus.PAYED);

        //when
        PagedResult<OrderDTO> orders = orderService.searchOrders(request, PageRequest.of(0, 5));

        //then
        assertEquals(1, orders.getContent().size());
        assertEquals(1L, orders.getTotalElements());
        assertEquals(0, orders.getPage());
        assertEquals(1, orders.getTotalPages());
    }

    private CreateOrderRequest prepareRequest() {

        AddressDTO shippingAddress = AddressDTO.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .zip("zip")
                .country("Country")
                .city("City")
                .street("Street")
                .phone("+7-951-5556-685")
                .build();

        List<OrderItemDTO> items = Arrays.asList(
                new OrderItemDTO("abc", 2),
                new OrderItemDTO("xyz", 3)
        );

        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingAddress(shippingAddress);
        request.setItems(items);
        return request;
    }
}
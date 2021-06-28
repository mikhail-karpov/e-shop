package com.mikhailkarpov.eshop.orders.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhailkarpov.eshop.orders.BaseIT;
import com.mikhailkarpov.eshop.orders.dto.AddressDTO;
import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequest;
import com.mikhailkarpov.eshop.orders.dto.OrderItemDTO;
import com.mikhailkarpov.eshop.orders.persistence.entities.Address;
import com.mikhailkarpov.eshop.orders.persistence.entities.Order;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderItem;
import com.mikhailkarpov.eshop.orders.persistence.repositories.OrderRepository;
import com.mikhailkarpov.eshop.orders.utils.DtoUtils;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.*;

import static com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus.ACCEPTED;
import static com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus.CONFIRMED;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIT extends BaseIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        //given
        OrderItem abcItem = new OrderItem("abc", 2);
        OrderItem xyzItem = new OrderItem("xyz", 3);

        Set<OrderItem> itemSet = new HashSet<>();
        itemSet.add(abcItem);
        itemSet.add(xyzItem);

        Address address = Address.builder()
                .firstName("First")
                .lastName("Last")
                .zip("zip")
                .country("Country")
                .city("City")
                .street("Street")
                .phone("Phone")
                .build();

        order1 = orderRepository.save(new Order("customerId", address, ACCEPTED, itemSet));
        order2 = orderRepository.save(new Order("customerId", address, CONFIRMED, Collections.singleton(xyzItem)));
    }

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
    }

    @Test
    void whenCreateOrderAndGetOrderById_thenFound() throws JsonProcessingException {
        //given
        AddressDTO validAddress = DtoUtils.getValidAddress();
        List<OrderItemDTO> items = Arrays.asList(
                new OrderItemDTO("abc", 2),
                new OrderItemDTO("xyz", 3)
        );
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setShippingAddress(validAddress);
        createOrderRequest.setItems(items);

        //@formatter:off
        ExtractableResponse<Response> response = RestAssured
            .given()
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(createOrderRequest))
            .when()
                .post("http://localhost:" + port + "/orders")
            .then()
                .statusCode(201)
            .extract();

        RestAssured
            .when()
                .get(response.header("Location"))
            .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("customerId", notNullValue())
                .body("status", equalToIgnoringCase("ACCEPTED"))
                .body("items.size()", equalTo(2))
                .body("shippingAddress.zip", equalTo(validAddress.getZip()))
                .body("shippingAddress.country", equalTo(validAddress.getCountry()))
                .body("shippingAddress.city", equalTo(validAddress.getCity()))
                .body("shippingAddress.street", equalTo(validAddress.getStreet()))
                .body("shippingAddress.phone", equalTo(validAddress.getPhone()))
                .body("shippingAddress.firstName", equalTo(validAddress.getFirstName()))
                .body("shippingAddress.lastName", equalTo(validAddress.getLastName()));
        //@formatter:on
    }

    @Test
    void givenOrders_whenFindAll_thenFound() {
        RestAssured
                .when()
                .get("http://localhost:" + port + "/orders")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(2))
                .body("page", equalTo(0))
                .body("totalPages", equalTo(1))
                .body("totalElements", equalTo(2));
    }

    @Test
    void givenOrders_whenSearchByCustomerAndStatus_thenFound() {
        //@formatter:off
        RestAssured
            .when()
                .get("http://localhost:" + port + "/orders?customer=customerId&status=ACCEPTED")
            .then()
                .statusCode(200)
                .body("content.size()", equalTo(1))
                .body("content[0].id", equalTo(order1.getId().toString()))
                .body("content[0].customerId", equalTo("customerId"))
                .body("content[0].shippingAddress.zip", equalTo("zip"))
                .body("content[0].shippingAddress.country", equalTo("Country"))
                .body("content[0].shippingAddress.city", equalTo("City"))
                .body("content[0].shippingAddress.street", equalTo("Street"))
                .body("content[0].shippingAddress.phone", equalTo("Phone"))
                .body("content[0].shippingAddress.firstName", equalTo("First"))
                .body("content[0].shippingAddress.lastName", equalTo("Last"))
                .body("page", equalTo(0))
                .body("totalPages", equalTo(1))
                .body("totalElements", equalTo(1));
        //@formatter:on
    }
}
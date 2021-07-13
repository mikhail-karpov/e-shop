package com.mikhailkarpov.eshop.productservice.web.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public class AbstractControllerTest {

    @MockBean
    private JwtDecoder jwtDecoder;
}

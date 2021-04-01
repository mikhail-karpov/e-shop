package com.mikhailkarpov.eshop.shoppingcartservice.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.Bean;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@TestConfiguration
public class ProductServiceMockServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer productService() {
        return new WireMockServer(options().dynamicPort());
    }

    @Autowired
    private WireMockServer productService;

    @Bean
    public ServerList<Server> serverList() {
        return new StaticServerList<>(new Server("localhost", productService.port()));
    }
}

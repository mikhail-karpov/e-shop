package com.mikhailkarpov.eshop.shoppingcartservice.client;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.FeignException;
import feign.Logger;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

public class ProductServiceClientConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {

        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {

            HttpStatus status = HttpStatus.resolve(response.status());

            HttpHeaders headers = new HttpHeaders();
            response.headers().forEach((k, v) -> headers.put(k, new ArrayList<>(v)));

            byte[] body = getBody(response);

            if (status.is4xxClientError() && !status.equals(HttpStatus.NOT_FOUND)) {

                String statusText = status.getReasonPhrase();
                HttpClientErrorException ex =
                        new HttpClientErrorException(status, statusText, body, null);
                return new HystrixBadRequestException(statusText, ex);
            }

            return FeignException.errorStatus(methodKey, response);
        };
    }

    private byte[] getBody(Response response) {

        if (response.body() == null) {
            return null;
        }

        try (InputStream inputStream = response.body().asInputStream()) {
            return IOUtils.toByteArray(inputStream);

        } catch (IOException ignored) {
            return null;
        }
    }
}

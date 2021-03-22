package com.mikhailkarpov.eshop.productservice.web.dto;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class PagedResultTest {

    @Autowired
    private JacksonTester<PagedResult<Product>> tester;

    @Test
    void testSerialization() throws IOException {

        List<Product> productsList = Arrays.asList(
                new Product("abc", "product 1", "product 1 description", 1000, 12),
                new Product("xyz", "product 2", "product 2 description", 2000, 22)
        );
        Page<Product> productsPage = new PageImpl<>(productsList, PageRequest.of(9, 2), 20);
        PagedResult<Product> pagedResult = new PagedResult<>(productsPage);

        String json = "{" +
                "\"result\":" +
                "[" +
                "{\"code\":\"abc\"," +
                "\"title\":\"product 1\"," +
                "\"description\":\"product 1 description\"," +
                "\"price\":1000,\"quantity\":12}," +
                "{\"code\":\"xyz\"," +
                "\"title\":\"product 2\"," +
                "\"description\":\"product 2 description\"," +
                "\"price\":2000,\"quantity\":22}" +
                "]," +
                "\"page\":9," +
                "\"total_results\":20," +
                "\"total_pages\":10" +
                "}";

        assertThat(tester.write(pagedResult)).isEqualToJson(json);
    }

}
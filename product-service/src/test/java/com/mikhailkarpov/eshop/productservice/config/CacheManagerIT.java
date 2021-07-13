package com.mikhailkarpov.eshop.productservice.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CacheManagerIT extends AbstractIT {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(cacheManager);
        Assertions.assertTrue(cacheManager instanceof RedisCacheManager);
    }
}

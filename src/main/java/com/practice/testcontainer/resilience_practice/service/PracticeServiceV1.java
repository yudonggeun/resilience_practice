package com.practice.testcontainer.resilience_practice.service;

import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PracticeServiceV1 {

    @Cacheable(cacheNames = "cache", key = "'v1'")
    public String getString() {
        return "run service at v1 : value=" + UUID.randomUUID();
    }
}

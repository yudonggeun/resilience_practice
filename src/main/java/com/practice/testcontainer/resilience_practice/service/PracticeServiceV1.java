package com.practice.testcontainer.resilience_practice.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PracticeServiceV1 {

    @Cacheable(cacheNames = "cache", key = "'v1'")
    public String getString(String string) {
        return string;
    }

    @CachePut(cacheNames = "cache", key = "'v1'")
    public String putString(String string){
        return string;
    }
}

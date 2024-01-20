package com.practice.testcontainer.resilience_practice.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory factory) {
        SerializationPair<Object> jsonSerialization = SerializationPair.fromSerializer(
            RedisSerializer.json());

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(jsonSerialization);

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}

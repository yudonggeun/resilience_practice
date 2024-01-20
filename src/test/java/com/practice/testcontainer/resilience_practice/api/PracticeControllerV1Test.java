package com.practice.testcontainer.resilience_practice.api;

import static org.assertj.core.api.Assertions.setRemoveAssertJRelatedElementsFromStackTrace;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import com.practice.testcontainer.resilience_practice.service.PracticeServiceV1;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class PracticeControllerV1Test {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis")
        .withExposedPorts(6379);

    /**
     * spring data redis 의존성을 사용했기 때문에 스펙에 맞게 설정하기
     */
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    PracticeServiceV1 service;

    @BeforeEach
    void run() {
        redis.start();
    }

    /**
     * test container를 이용하여 @Cacheable 동작확인하기 테스트를 최대한 간단하게 만들고 학습을 위한 프로젝트이기 때문에 디버깅을 이용하여 redis에
     * 데이터가 입력되었는지 확인하기를 권장함(redisInsight)
     */
    @DisplayName("redis cache with test containers")
    @Test
    void test1() {
        // given
        String input = "run service at v1 : value=" + UUID.randomUUID();
        System.out.println("input = " + input);
        // when
        service.putString(input);
        String result = service.getString("failed");
        // break point : check test container(redis)
        System.out.println("result = " + result);
        then(result).isEqualTo(input);
    }

    @DisplayName("if redis is not running, then throw timeout exception")
    @Test
    void test2() {
        // given
        String expected = "not cached";
        // when
        service.putString("cache");
        redis.stop();
        // then
        thenThrownBy(() -> service.getString(expected))
            .isInstanceOf(QueryTimeoutException.class);
    }
}
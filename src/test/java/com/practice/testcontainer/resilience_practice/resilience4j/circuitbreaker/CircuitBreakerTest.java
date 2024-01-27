package com.practice.testcontainer.resilience_practice.resilience4j.circuitbreaker;

import static org.assertj.core.api.Assertions.*;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.functions.CheckedSupplier;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.decorators.Decorators.DecorateSupplier;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CircuitBreakerTest {

    @DisplayName("Circuit Breaker Registry 만들기")
    @Test
    void createCircuitBreakerRegistry() {
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
    }

    @DisplayName("Circuit Breaker Registry 코드로 설정하기")
    @Test
    void test() {
        // Create a custom configuration for a CircuitBreaker
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .slowCallRateThreshold(50)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .slowCallDurationThreshold(Duration.ofSeconds(2))
            .permittedNumberOfCallsInHalfOpenState(3)
            .minimumNumberOfCalls(10)
            .slidingWindowType(SlidingWindowType.TIME_BASED)
            .slidingWindowSize(5)
            .recordExceptions(IOException.class, TimeoutException.class)
//            .ignoreExceptions(BusinessException.class, OtherBusinessException.class)
            .build();

        // Create a CircuitBreakerRegistry with a custom global configuration
        CircuitBreakerRegistry circuitBreakerRegistry =
            CircuitBreakerRegistry.of(circuitBreakerConfig);

        // Get or create a CircuitBreaker from the CircuitBreakerRegistry
        // with the global default configuration
        CircuitBreaker circuitBreakerWithDefaultConfig =
            circuitBreakerRegistry.circuitBreaker("name1");

        // Get or create a CircuitBreaker from the CircuitBreakerRegistry
        // with a custom configuration
        CircuitBreaker circuitBreakerWithCustomConfig = circuitBreakerRegistry
            .circuitBreaker("name2", circuitBreakerConfig);

        // Create a custom configuration for a CircuitBreaker
        CircuitBreaker temperCircuitBreaker = CircuitBreaker.of("temper", circuitBreakerConfig);
    }

    @DisplayName("map을 이용하여 성공 이후 행위를 정의할 수 있다.")
    @Test
    void whenMethodSuccessThenMap() {
        // Given
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");

        // When I decorate my function
        Supplier<String> decoratedSupplier = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> "This can be any method which returns: 'Hello");

        // try
        Try<String> result = Try.ofSupplier(decoratedSupplier)
            .map(value -> value + " world'");

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isEqualTo("This can be any method which returns: 'Hello world'");
    }

    @DisplayName("성공시 행위를 정의하기")
    @Test
    void whenMethodSuccessHandle() {
        // Given
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");

        // business logic
        Supplier<String> businessFunction = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> "This can be any method which returns: 'Hello");
        // on success handle logic
        Consumer<String> onSuccessFunction = returnObject -> System.out.println(
            "returnObject = " + returnObject);
        // try
        Try<String> result = Try.ofSupplier(businessFunction)
            .onSuccess(onSuccessFunction);

        // Then
        assertThat(result.isSuccess()).isTrue();
    }

    @DisplayName("실패시 행위를 정의하기")
    @Test
    void whenMethodFailHandle() {
        // Given
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");

        // business logic
        Supplier<String> businessFunction = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> {
                throw new RuntimeException("my test");
            });
        // on fail handle logic
        Consumer<Throwable> onFailureFunction = throwable -> System.out.println(
            "throwable = " + throwable);
        // try
        Try<String> result = Try.ofSupplier(businessFunction)
            .onFailure(onFailureFunction);

        // Then
        assertThat(result.isFailure()).isTrue();
    }

    @DisplayName("실패시 리턴 값 변경")
    @Test
    void whenMethodFailRecover() {
        // Given
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");

        // business logic
        Supplier<String> businessFunction = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> {
                throw new RuntimeException("my test");
            });
        // on fail handle logic
        Consumer<Throwable> onFailureFunction = throwable -> System.out.println(
            "throwable = " + throwable);
        // try
        Try<String> result = Try.ofSupplier(businessFunction)
            .onFailure(onFailureFunction)
            .recover(RuntimeException.class, (e) -> "recover hello");

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isEqualTo("recover hello");
    }

    @DisplayName("이벤트 기반 핸들링")
    @Test
    void circuitBreakerEventHandle() {
        // Given
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");
        circuitBreaker.getEventPublisher()
            .onError(event -> {
                System.out.println("on error");
                System.out.println(event.getEventType());
                System.out.println(event.getThrowable().getMessage());
                System.out.println(event.getElapsedDuration());
                System.out.println(event.getCreationTime());
            })
            .onSuccess(event -> {
                System.out.println("on success");
                System.out.println(event.getEventType());
                System.out.println(event.getCircuitBreakerName());
                System.out.println(event.getElapsedDuration());
                System.out.println(event.getCreationTime());
            });

        // business logic
        Supplier<String> businessFunction = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> {
                throw new RuntimeException("my test");
            });
        // try
        Try<String> result = Try.ofSupplier(businessFunction);

        // Then
        assertThat(result.isFailure()).isTrue();
    }

    @DisplayName("decorate ohter core")
    @Test
    void decorate() {
        // given
        Supplier<String> businessLogic = () -> {
            System.out.println("run business logic");
            throw new RuntimeException();
        };

        CircuitBreaker circuitBreaker = CircuitBreakerRegistry.ofDefaults()
            .circuitBreaker("test1");

        circuitBreaker.getEventPublisher()
            .onError(event -> System.out.println("circuit breaker success"))
            .onSuccess(event -> System.out.println("circuit breaker success"));

        Retry retry = RetryRegistry.ofDefaults()
            .retry("retry test");

        retry.getEventPublisher()
            .onError(event -> System.out.println("retry fail"))
            .onSuccess(event -> System.out.println("retry success"));
        // when
        DecorateSupplier<String> finalSupplier = Decorators.ofSupplier(businessLogic)
            .withCircuitBreaker(circuitBreaker)
            .withRetry(retry)
            .withFallback(throwable -> "fall back");
        // then
        String result = finalSupplier.get();
        System.out.println("result = " + result);
    }
}

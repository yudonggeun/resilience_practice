package com.practice.testcontainer.resilience_practice.testcontainer;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RedisContainerTest {

    @Container
    static GenericContainer<?> container = new GenericContainer<>("redis")
        .withEnv(Map.of(
            "HELLO", "hi",
            "TEST", "test"
        ))
        .withExposedPorts(6379);

    /**
     * test container를 통해서 도커 컨테이너가 가동되는지 확인한다.
     */
    @DisplayName("test container를 이용하여 컨테이너 동작 확인하기")
    @Test
    void pleaseRunInDebugMode() {

        System.out.println("isRunning : " + container.isRunning());
        System.out.println("getBinds() : " + container.getBinds());
        System.out.println("getVolumesFroms() " + container.getVolumesFroms());
        System.out.println("getEnvMap()" + container.getEnvMap());
        System.out.println(
            "getMappedPort(..) : " + container.getMappedPort(6379)); // 6379에 연결된 포트번호를 조회
        System.out.println("getFirstMappedPort() : " + container.getFirstMappedPort());
        System.out.println("getContainersInfo() : " + container.getContainerInfo());
    }
}

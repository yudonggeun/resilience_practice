package com.practice.testcontainer.resilience_practice.restclient;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@Testcontainers(disabledWithoutDocker = true)
class WireMockTestContainerRestClientTest {

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:2.35.0")
        .withMappingFromResource("mocks-config.json")
        .withFileFromResource("album-photos-response.json");

    /**
     * resource에 wire mock의 API SPEC을 정의하여 API 서버를 사용하는 방식
     */
    @Test
    void test() {
        Long albumId = 1L;
        RestClient restClient = RestClient.builder()
            .baseUrl(
                "http://localhost:" + wiremockServer.getPort() + "/albums/" + albumId + "/photos")
            .build();

        ResponseEntity<String> response = restClient.get()
            .header("Content-Type", "application/json")
            .retrieve()
            .toEntity(String.class);

        System.out.println(response);
    }
}
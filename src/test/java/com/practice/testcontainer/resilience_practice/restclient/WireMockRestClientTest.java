package com.practice.testcontainer.resilience_practice.restclient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.BDDAssertions.then;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest
public class WireMockRestClientTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(
            wireMockConfig()
                .dynamicPort()
        )
        .build();

    @DisplayName("mocking api server")
    @Test
    void test() {
        // given
        RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:" + wireMock.getPort() + "/")
            .build();

        String mockResponseBody = """
            {
                "mock": "hello world"
            }
            """;

        wireMock.stubFor(WireMock.get("/")
            .willReturn(aResponse()
                .withBody(mockResponseBody))
        );

        // when
        String body = restClient.get()
            .retrieve()
            .body(String.class);

        // then
        System.out.println("response = " + body);
        then(body).isEqualTo(mockResponseBody);
    }
}

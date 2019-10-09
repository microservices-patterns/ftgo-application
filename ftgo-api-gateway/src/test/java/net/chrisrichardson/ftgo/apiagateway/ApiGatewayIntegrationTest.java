package net.chrisrichardson.ftgo.apiagateway;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import net.chrisrichardson.ftgo.apiagateway.orders.OrderDetails;
import net.chrisrichardson.ftgo.apiagateway.proxies.OrderInfo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiGatewayIntegrationTestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties={"order.destinations.orderServiceUrl=http://localhost:${wiremock.server.port}",
                "order.destinations.orderHistoryServiceUrl=http://localhost:8083",
                "consumer.destinations.consumerServiceUrl=http://localhost:9999"
                  })
@AutoConfigureWireMock(port=0)
public class ApiGatewayIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    public void shouldProxyCreateOrder() {

        String expectedResponse = "{}";

        stubFor(post(urlEqualTo("/orders"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));


        WebClient client = WebClient.create("http://localhost:" + port + "/orders");

        ResponseEntity<String> z = client
                .post()
                .body(BodyInserters.fromObject("{}"))
                .exchange()
                .flatMap(r -> r.toEntity(String.class))
                .block();

        assertNotNull(z);
        assertEquals(HttpStatus.OK, z.getStatusCode());
        assertEquals(expectedResponse, z.getBody());

        verify(postRequestedFor(urlMatching("/orders")));

    }

    @Test
    public void shouldProxyGetOrderDetails() throws JsonProcessingException {

        String orderId = "1";

        OrderDetails expectedOrderDetails = new OrderDetails(new OrderInfo(orderId, "CREATED"));
        ObjectMapper objectMapper = new ObjectMapper();

        String body = objectMapper.writeValueAsString(expectedOrderDetails.getOrderInfo());

        String expectedPath = "/orders/" + orderId;

        stubFor(get(urlEqualTo(expectedPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withBody(body)));


        WebClient client = WebClient.create("http://localhost:" + port + "/orders/1");

        ResponseEntity<OrderDetails> z = client
                .get()
                .exchange()
                .flatMap(r -> r.toEntity(OrderDetails.class))
                .block();

        assertNotNull(z);
        assertEquals(HttpStatus.OK, z.getStatusCode());
        assertEquals(body, expectedOrderDetails, z.getBody());

        verify(getRequestedFor(urlMatching(expectedPath)));

    }

}

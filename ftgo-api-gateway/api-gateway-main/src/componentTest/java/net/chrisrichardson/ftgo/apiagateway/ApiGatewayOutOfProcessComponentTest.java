package net.chrisrichardson.ftgo.apiagateway;

import io.eventuate.testcontainers.service.ServiceContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiGatewayOutOfProcessComponentTest {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayOutOfProcessComponentTest.class);
    private String baseUri;

    static Network network = Network.newNetwork();

    static GenericContainer<?> service =
            new ServiceContainer(new ImageFromDockerfile()
                    .withFileFromPath(".", Paths.get(".").toAbsolutePath())
                    .withDockerfilePath("Dockerfile")
                    .withBuildArg("baseImageVersion", "0.1.0.RELEASE"))
                    .withNetwork(network)
                    .withEnv("ORDER_DESTINATIONS_ORDERSERVICEURL", "http://localhost:8080")
                    .withEnv("ORDER_DESTINATIONS_ORDERHISTORYSERVICEURL", "http://localhost:8080")
                    .withEnv("CONSUMER_DESTINATIONS_CONSUMERSERVICEURL", "http://localhost:8080")
                    .withReuse(false)
                    .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC api-gateway"));

    @BeforeAll
    static void startContainers() {
        service.start();
    }

    @BeforeEach
    void setup() {
        baseUri = String.format("http://localhost:%d", service.getFirstMappedPort());
    }

    @Test
    void shouldStart() {
        assertThat(service.isRunning()).isTrue();
        assertThat(service.getFirstMappedPort()).isNotNull();
    }

    @Test
    void healthEndpointReturnsOk() {
        RestAssured.given()
                .baseUri(baseUri)
                .when()
                .get("/actuator/health")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString()
                .contains("UP");
    }
}

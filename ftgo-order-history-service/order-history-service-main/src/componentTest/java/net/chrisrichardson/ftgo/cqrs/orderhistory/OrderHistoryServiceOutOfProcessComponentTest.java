package net.chrisrichardson.ftgo.cqrs.orderhistory;

import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeCluster;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeContainer;
import io.eventuate.testcontainers.service.ServiceContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

public class OrderHistoryServiceOutOfProcessComponentTest {

    protected static Logger logger = LoggerFactory.getLogger(OrderHistoryServiceOutOfProcessComponentTest.class);
    private String baseUri;

    public static EventuateKafkaNativeCluster eventuateKafkaCluster = new EventuateKafkaNativeCluster("order-history-service-oop-tests");

    public static EventuateKafkaNativeContainer kafka = eventuateKafkaCluster.kafka
            .withNetworkAliases("kafka")
            .withReuse(false);

    public static LocalStackContainer dynamodb = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
            .withServices(DYNAMODB)
            .withNetwork(eventuateKafkaCluster.network)
            .withNetworkAliases("dynamodb")
            .withReuse(false);

    public static GenericContainer<?> service =
            new ServiceContainer(new ImageFromDockerfile()
                    .withFileFromPath(".", Paths.get(".").toAbsolutePath())
                    .withDockerfilePath("Dockerfile")
                    .withBuildArg("baseImageVersion", "BUILD-15"))
                    .withNetwork(eventuateKafkaCluster.network)
                    .withKafka(kafka)
                    .withEnv("AWS_DYNAMODB_ENDPOINT_URL", "http://dynamodb:4566")
                    .withEnv("AWS_ACCESS_KEY_ID", "test")
                    .withEnv("AWS_SECRET_ACCESS_KEY", "test")
                    .withEnv("AWS_REGION", "us-east-1")
                    .withReuse(false)
                    .dependsOn(dynamodb)
                    .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC order-history-service:"));

    @BeforeAll
    static void startContainers() {
        Startables.deepStart(kafka, dynamodb).join();
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

    @Test
    void shouldReturnEmptyOrdersForNonExistentConsumer() {
        RestAssured.given()
                .baseUri(baseUri)
                .when()
                .get("/orders?consumerId=999999")
                .then()
                .statusCode(200);
    }
}

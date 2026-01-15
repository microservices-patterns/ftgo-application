package net.chrisrichardson.ftgo.kitchenservice;

import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.EventuateVanillaPostgresContainer;
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
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startables;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class KitchenServiceOutOfProcessComponentTest {

    private static final Logger logger = LoggerFactory.getLogger(KitchenServiceOutOfProcessComponentTest.class);
    private String baseUri;

    static EventuateKafkaNativeCluster eventuateKafkaCluster = new EventuateKafkaNativeCluster("kitchen-service-oop-tests");

    static EventuateKafkaNativeContainer kafka = eventuateKafkaCluster.kafka
            .withNetworkAliases("kafka")
            .withReuse(false);

    static EventuateDatabaseContainer<?> database = new EventuateVanillaPostgresContainer()
            .withNetwork(eventuateKafkaCluster.network)
            .withNetworkAliases("database")
            .withReuse(false);

    static GenericContainer<?> service =
            new ServiceContainer(new ImageFromDockerfile()
                    .withFileFromPath(".", Paths.get(".").toAbsolutePath())
                    .withDockerfilePath("Dockerfile")
                    .withBuildArg("baseImageVersion", "BUILD-15"))
                    .withNetwork(eventuateKafkaCluster.network)
                    .withDatabase(database)
                    .withKafka(kafka)
                    .withEnv("SPRING_PROFILES_ACTIVE", "postgres")
                    .withEnv("SPRING_JPA_HIBERNATE_DDL_AUTO", "update")
                    .withReuse(false)
                    .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC kitchen-service:"));

    @BeforeAll
    static void startContainers() {
        Startables.deepStart(kafka, database, service).join();
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

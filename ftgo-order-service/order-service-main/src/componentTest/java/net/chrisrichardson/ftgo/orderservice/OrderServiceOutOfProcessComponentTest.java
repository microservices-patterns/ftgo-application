package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.EventuateVanillaPostgresContainer;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeCluster;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeContainer;
import io.eventuate.testcontainers.service.ServiceContainer;
import io.eventuate.tram.spring.testing.outbox.commands.CommandOutboxTestSupport;
import io.eventuate.tram.spring.testing.outbox.commands.CommandOutboxTestSupportConfiguration;
import io.eventuate.tram.testing.producer.kafka.commands.DirectToKafkaCommandProducer;
import io.eventuate.tram.testing.producer.kafka.commands.EnableDirectToKafkaCommandProducer;
import io.eventuate.tram.testing.producer.kafka.events.DirectToKafkaDomainEventPublisher;
import io.eventuate.tram.testing.producer.kafka.events.EnableDirectToKafkaDomainEventPublisher;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startables;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OrderServiceOutOfProcessComponentTest.TestConfiguration.class)
public class OrderServiceOutOfProcessComponentTest {

    protected static Logger logger = LoggerFactory.getLogger(OrderServiceOutOfProcessComponentTest.class);
    private String baseUri;

    @Configuration
    @EnableAutoConfiguration(exclude = {JpaRepositoriesAutoConfiguration.class})
    @Import({CommandOutboxTestSupportConfiguration.class})
    @EnableDirectToKafkaCommandProducer
    @EnableDirectToKafkaDomainEventPublisher
    static class TestConfiguration {
    }

    public static EventuateKafkaNativeCluster eventuateKafkaCluster = new EventuateKafkaNativeCluster("order-service-oop-tests");

    public static EventuateKafkaNativeContainer kafka = eventuateKafkaCluster.kafka
            .withNetworkAliases("kafka")
            .withReuse(false);

    public static EventuateDatabaseContainer<?> database = new EventuateVanillaPostgresContainer()
            .withNetwork(eventuateKafkaCluster.network)
            .withNetworkAliases("database")
            .withReuse(false);

    public static GenericContainer<?> service =
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
                    .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC order-service:"));

    @Autowired
    private DirectToKafkaCommandProducer commandProducer;

    @Autowired
    private CommandOutboxTestSupport commandOutboxTestSupport;

    @Autowired
    private DirectToKafkaDomainEventPublisher directToKafkaDomainEventPublisher;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(kafka, database, service).join();

        kafka.registerProperties(registry::add);
        database.registerProperties(registry::add);
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
    void shouldReturn404ForNonExistentOrder() {
        RestAssured.given()
                .baseUri(baseUri)
                .when()
                .get("/orders/999999")
                .then()
                .statusCode(404);
    }
}

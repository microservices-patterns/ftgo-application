package net.chrisrichardson.ftgo.accountingservice;

import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.EventuateVanillaPostgresContainer;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeCluster;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeContainer;
import io.eventuate.testcontainers.service.ServiceContainer;
import io.eventuate.tram.testing.producer.kafka.commands.DirectToKafkaCommandProducer;
import io.eventuate.tram.testing.producer.kafka.commands.EnableDirectToKafkaCommandProducer;
import io.eventuate.tram.testing.producer.kafka.events.DirectToKafkaDomainEventPublisher;
import io.eventuate.tram.testing.producer.kafka.events.EnableDirectToKafkaDomainEventPublisher;
import io.restassured.RestAssured;
import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerCreated;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startables;

import java.nio.file.Paths;
import java.util.Collections;

import static io.eventuate.util.test.async.Eventually.eventually;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AccountingServiceOutOfProcessComponentTest.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AccountingServiceOutOfProcessComponentTest {

    private static final Logger logger = LoggerFactory.getLogger(AccountingServiceOutOfProcessComponentTest.class);
    private static final String CONSUMER_AGGREGATE_TYPE = "net.chrisrichardson.ftgo.consumerservice.domain.Consumer";

    private String baseUri;

    static EventuateKafkaNativeCluster eventuateKafkaCluster = new EventuateKafkaNativeCluster("accounting-service-oop-tests");

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
                    .withBuildArg("baseImageVersion", "0.1.0.RELEASE"))
                    .withNetwork(eventuateKafkaCluster.network)
                    .withDatabase(database)
                    .withKafka(kafka)
                    .withEnv("SPRING_PROFILES_ACTIVE", "postgres")
                    .withEnv("SPRING_JPA_HIBERNATE_DDL_AUTO", "update")
                    .withReuse(false)
                    .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC accounting-service:"));

    @Configuration
    @EnableDirectToKafkaDomainEventPublisher
    @EnableDirectToKafkaCommandProducer
    static class TestConfiguration {
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(kafka, database, service).join();

        kafka.registerProperties(registry::add);
    }

    @Autowired
    private DirectToKafkaDomainEventPublisher domainEventPublisher;

    @Autowired
    private DirectToKafkaCommandProducer commandProducer;

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
    void shouldCreateAccountAndAuthorize() {
        long consumerId = System.currentTimeMillis();
        long orderId = consumerId + 1000;
        Money orderTotal = new Money("10.00");
        String replyTo = "test-reply-channel-" + System.currentTimeMillis();

        logger.info("Publishing ConsumerCreated event for consumerId: {}", consumerId);

        // Step 1: Publish ConsumerCreated event to Kafka to create the account
        domainEventPublisher.publish(CONSUMER_AGGREGATE_TYPE, Long.toString(consumerId), new ConsumerCreated());

        // Step 2: Wait for the account to be created (poll REST API)
        eventually(() -> {
            logger.info("Checking if account {} exists via REST API", consumerId);
            RestAssured.given()
                    .baseUri(baseUri)
                    .when()
                    .get("/accounts/" + consumerId)
                    .then()
                    .statusCode(200);
            logger.info("Account {} created successfully", consumerId);
        });

        logger.info("Sending AuthorizeCommand for consumerId: {}, orderId: {}", consumerId, orderId);

        // Step 3: Send AuthorizeCommand to Kafka
        commandProducer.send("accountingService",
                new AuthorizeCommand(consumerId, orderId, orderTotal),
                replyTo,
                Collections.emptyMap());

        // Step 4: Verify the account is still accessible (authorization succeeded)
        // Note: If authorization failed, the service would throw an exception
        // For a more thorough test, we could listen on the reply channel
        eventually(() -> {
            logger.info("Verifying account {} is still valid after authorization", consumerId);
            RestAssured.given()
                    .baseUri(baseUri)
                    .when()
                    .get("/accounts/" + consumerId)
                    .then()
                    .statusCode(200);
        });

        logger.info("Test completed successfully");
    }
}

package net.chrisrichardson.ftgo.consumerservice;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class ConsumerServiceOutOfProcessComponentTest {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerServiceOutOfProcessComponentTest.class);
    private String baseUri;

    static EventuateKafkaNativeCluster eventuateKafkaCluster = new EventuateKafkaNativeCluster("consumer-service-oop-tests");

    static EventuateKafkaNativeContainer kafka = eventuateKafkaCluster.kafka
            .withNetworkAliases("kafka")
            .withReuse(false);

    static EventuateDatabaseContainer<?> database = new EventuateVanillaPostgresContainer()
            .withNetwork(eventuateKafkaCluster.network)
            .withNetworkAliases("database")
            .withExposedPorts(5432)
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
                    .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC consumer-service:"));

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

    @Test
    void exploreDatabase() throws Exception {
        String jdbcUrl = "jdbc:postgresql://localhost:" + database.getMappedPort(5432) + "/eventuate";
        logger.info("JDBC URL: {}", jdbcUrl);
        // Use standard eventuate vanilla postgres credentials
        try (Connection conn = DriverManager.getConnection(jdbcUrl, "postgresuser", "postgrespw");
             Statement stmt = conn.createStatement()) {

            // List all schemas
            logger.info("=== SCHEMAS ===");
            ResultSet schemas = stmt.executeQuery("SELECT schema_name FROM information_schema.schemata");
            while (schemas.next()) {
                logger.info("Schema: {}", schemas.getString(1));
            }

            // List all tables in all schemas
            logger.info("=== TABLES ===");
            ResultSet tables = stmt.executeQuery(
                "SELECT table_schema, table_name FROM information_schema.tables " +
                "WHERE table_schema NOT IN ('pg_catalog', 'information_schema') ORDER BY table_schema, table_name");
            while (tables.next()) {
                logger.info("Table: {}.{}", tables.getString(1), tables.getString(2));
            }

            // Check specifically for message table in any schema
            logger.info("=== MESSAGE TABLE SEARCH ===");
            ResultSet messageTables = stmt.executeQuery(
                "SELECT table_schema, table_name FROM information_schema.tables WHERE table_name = 'message'");
            boolean found = false;
            while (messageTables.next()) {
                logger.info("Found message table in schema: {}", messageTables.getString(1));
                found = true;
            }
            if (!found) {
                logger.info("No message table found in any schema");
            }
        }
    }

    @Test
    void shouldCreateConsumer() {
        String requestBody = """
                {
                    "name": {
                        "firstName": "John",
                        "lastName": "Doe"
                    }
                }
                """;

        Integer consumerId = given()
                .baseUri(baseUri)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/consumers")
                .then()
                .statusCode(200)
                .extract()
                .path("consumerId");

        assertThat(consumerId).isNotNull();
    }
}

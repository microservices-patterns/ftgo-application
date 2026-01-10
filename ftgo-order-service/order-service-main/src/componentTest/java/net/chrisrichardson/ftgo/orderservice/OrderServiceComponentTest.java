package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeCluster;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeContainer;
import io.restassured.RestAssured;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.domain.MenuItem;
import net.chrisrichardson.ftgo.orderservice.domain.Restaurant;
import net.chrisrichardson.ftgo.orderservice.domain.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = OrderServiceComponentTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceComponentTest {

    private static final EventuateKafkaNativeCluster eventuateKafkaCluster;
    private static final EventuateKafkaNativeContainer kafka;
    private static final PostgreSQLContainer<?> database;

    static {
        eventuateKafkaCluster = new EventuateKafkaNativeCluster("order-service-component-test");
        kafka = eventuateKafkaCluster.kafka
                .withNetworkAliases("kafka")
                .withReuse(true);
        database = new PostgreSQLContainer<>("postgres:16")
                .withNetwork(eventuateKafkaCluster.network)
                .withNetworkAliases("database")
                .withReuse(true);
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(kafka, database).join();

        kafka.registerProperties(registry::add);
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void shouldReturn404ForNonExistentOrder() {
        given()
            .when()
                .get("/orders/999")
            .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnActuatorHealth() {
        given()
            .when()
                .get("/actuator/health")
            .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void shouldSaveAndRetrieveRestaurant() {
        // Create a restaurant directly in the database
        long restaurantId = System.currentTimeMillis();
        String restaurantName = "Test Restaurant";
        Restaurant restaurant = new Restaurant(restaurantId, restaurantName,
                Collections.singletonList(new MenuItem("1", "Test Item", new Money("10.00"))));
        restaurantRepository.save(restaurant);

        // Verify it was saved (this tests the JPA/database integration)
        Restaurant found = restaurantRepository.findById(restaurantId).orElseThrow();
        assert found.getName().equals(restaurantName);
    }
}

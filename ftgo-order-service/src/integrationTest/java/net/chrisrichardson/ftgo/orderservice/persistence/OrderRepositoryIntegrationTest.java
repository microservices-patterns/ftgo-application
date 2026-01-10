package net.chrisrichardson.ftgo.orderservice.persistence;

import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.domain.DeliveryInformation;
import net.chrisrichardson.ftgo.orderservice.domain.MenuItem;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRepository;
import net.chrisrichardson.ftgo.orderservice.domain.Restaurant;
import net.chrisrichardson.ftgo.orderservice.domain.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryIntegrationTest.class);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void shouldSaveAndFindOrder() {
        // Given
        long consumerId = 1L;
        long restaurantId = 1L;
        String restaurantName = "Test Restaurant";

        // Create restaurant first
        Restaurant restaurant = new Restaurant(restaurantId, restaurantName,
            Collections.singletonList(new MenuItem("1", "Chicken Vindaloo", new Money("12.34"))));
        restaurantRepository.save(restaurant);
        logger.info("Created restaurant: {} with ID: {}", restaurantName, restaurantId);

        // Create order
        Address deliveryAddress = new Address("123 Main St", null, "Oakland", "CA", "94612");
        DeliveryInformation deliveryInfo = new DeliveryInformation(LocalDateTime.now().plusHours(1), deliveryAddress);
        List<OrderLineItem> lineItems = Collections.singletonList(
            new OrderLineItem("1", "Chicken Vindaloo", new Money("12.34"), 2)
        );

        Order order = new Order(consumerId, restaurantId, deliveryInfo, lineItems);

        // When
        Order savedOrder = orderRepository.save(order);
        logger.info("Created order with ID: {}", savedOrder.getId());

        // Then
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getConsumerId()).isEqualTo(consumerId);
        assertThat(foundOrder.get().getRestaurantId()).isEqualTo(restaurantId);
    }

    @Test
    void shouldSaveRestaurant() {
        // Given
        long restaurantId = System.currentTimeMillis();
        String restaurantName = "Ajanta";
        List<MenuItem> menuItems = Collections.singletonList(
            new MenuItem("1", "Chicken Vindaloo", new Money("12.34"))
        );

        // When
        Restaurant restaurant = new Restaurant(restaurantId, restaurantName, menuItems);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        logger.info("Created restaurant: {} with ID: {}", restaurantName, savedRestaurant.getId());

        // Then
        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(savedRestaurant.getId());
        assertThat(foundRestaurant).isPresent();
        assertThat(foundRestaurant.get().getName()).isEqualTo(restaurantName);
    }
}

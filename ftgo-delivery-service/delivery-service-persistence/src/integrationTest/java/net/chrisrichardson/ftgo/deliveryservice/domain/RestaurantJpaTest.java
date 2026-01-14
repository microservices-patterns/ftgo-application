package net.chrisrichardson.ftgo.deliveryservice.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = RestaurantJpaTest.Config.class)
@Testcontainers
class RestaurantJpaTest {

  @Configuration
  @EnableJpaRepositories
  @EnableAutoConfiguration
  public static class Config {
  }

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("eventuate")
          .withUsername("postgresuser")
          .withPassword("postgrespw");

  @DynamicPropertySource
  static void postgresProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }


  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Test
  public void shouldSaveAndLoad() {
    long restaurantId = System.currentTimeMillis();
    Restaurant restaurant = Restaurant.create(restaurantId, "Delicious Indian", DeliveryServiceTestData.PICKUP_ADDRESS);
    restaurantRepository.save(restaurant);

    transactionTemplate.execute((ts) -> {
      Restaurant loadedCourier = restaurantRepository.findById(restaurantId).get();
      assertEquals(DeliveryServiceTestData.PICKUP_ADDRESS, loadedCourier.getAddress());
      return null;
    });

  }
}

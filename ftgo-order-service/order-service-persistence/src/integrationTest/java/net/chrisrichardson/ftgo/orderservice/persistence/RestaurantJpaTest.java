package net.chrisrichardson.ftgo.orderservice.persistence;

import net.chrisrichardson.ftgo.orderservice.domain.Restaurant;
import net.chrisrichardson.ftgo.orderservice.domain.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = OrderPersistenceConfiguration.class)
public class RestaurantJpaTest {

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
  private RestaurantRepository restaurantRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Test
  public void shouldSaveAndLoadRestaurant() {
    long restaurantId = saveRestaurant();
    assertEquals(AJANTA_ID, restaurantId);
    loadRestaurant(restaurantId);
  }

  @Test
  public void shouldSaveRestaurantTwice() {
    long restaurantId1 = saveRestaurant();
    long restaurantId2 = saveRestaurant();
    assertEquals(AJANTA_ID, restaurantId1);
    assertEquals(restaurantId1, restaurantId2);
    loadRestaurant(restaurantId1);
  }

  private void loadRestaurant(long restaurantId) {
    transactionTemplate.execute(ts -> {
      Restaurant restaurant = restaurantRepository.findById(restaurantId).get();
      assertEquals(AJANTA_RESTAURANT_NAME, restaurant.getName());
      assertEquals(AJANTA_RESTAURANT_MENU_ITEMS, restaurant.getMenuItems());
      return null;
    });
  }


  private long saveRestaurant() {
    return transactionTemplate.execute((ts) -> {
        Restaurant restaurant = new Restaurant(AJANTA_ID, AJANTA_RESTAURANT_NAME, AJANTA_RESTAURANT_MENU_ITEMS);
        restaurantRepository.save(restaurant);
        return restaurant.getId();
      });
  }

}

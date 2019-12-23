package net.chrisrichardson.ftgo.orderservice.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderJpaTestConfiguration.class)
public class RestaurantJpaTest {

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

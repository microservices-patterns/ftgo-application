package net.chrisrichardson.ftgo.deliveryservice;

import net.chrisrichardson.ftgo.deliveryservice.domain.DeliveryServiceTestData;
import net.chrisrichardson.ftgo.deliveryservice.messaging.RestaurantEventMapper;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import net.chrisrichardson.ftgo.restaurantservice.events.Menu;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItem;

import java.util.Collections;
import java.util.List;

import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.Money;

public class RestaurantEventMother {

  public static final String CHICKEN_VINDALOO = "Chicken Vindaloo";
  public static final String CHICKEN_VINDALOO_MENU_ITEM_ID = "1";
  public static final String CHICKEN_VINDALOO_PRICE = "12.34";
  public static final Address RESTAURANT_ADDRESS = new Address("1 Main Street", "Unit 99", "Oakland", "CA", "94611");

  public static MenuItem CHICKEN_VINDALOO_MENU_ITEM = new MenuItem()
    .withId(CHICKEN_VINDALOO_MENU_ITEM_ID)
    .withName(CHICKEN_VINDALOO)
    .withPrice(CHICKEN_VINDALOO_PRICE);

  public static final List<MenuItem> AJANTA_RESTAURANT_MENU_ITEMS = Collections.singletonList(CHICKEN_VINDALOO_MENU_ITEM);


  static RestaurantCreated makeRestaurantCreated() {
    return new RestaurantCreated()
            .withName("Delicious Indian")
            .withAddress(RestaurantEventMapper.fromAddress(DeliveryServiceTestData.PICKUP_ADDRESS))
            .withMenu(new Menu().withMenuItems(AJANTA_RESTAURANT_MENU_ITEMS));
  }
}

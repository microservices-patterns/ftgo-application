package net.chrisrichardson.ftgo.orderservice;

import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.domain.MenuItem;
import net.chrisrichardson.ftgo.orderservice.domain.Restaurant;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;

import java.util.Collections;
import java.util.List;

public class RestaurantMother {
  public static final String AJANTA_RESTAURANT_NAME = "Ajanta";
  public static final long AJANTA_ID = 1L;

  public static final String CHICKEN_VINDALOO = "Chicken Vindaloo";
  public static final String CHICKEN_VINDALOO_MENU_ITEM_ID = "1";
  public static final Money CHICKEN_VINDALOO_PRICE = new Money("12.34");
  public static final Address RESTAURANT_ADDRESS = new Address("1 Main Street", "Unit 99", "Oakland", "CA", "94611");

  public static MenuItem CHICKEN_VINDALOO_MENU_ITEM = new MenuItem(CHICKEN_VINDALOO_MENU_ITEM_ID, CHICKEN_VINDALOO, CHICKEN_VINDALOO_PRICE);

  public static final List<MenuItem> AJANTA_RESTAURANT_MENU_ITEMS = Collections.singletonList(CHICKEN_VINDALOO_MENU_ITEM);

  public static final Restaurant AJANTA_RESTAURANT =
          new Restaurant(AJANTA_ID, AJANTA_RESTAURANT_NAME, AJANTA_RESTAURANT_MENU_ITEMS);

  public static RestaurantCreated makeAjantaRestaurantCreatedEvent() {
    List<net.chrisrichardson.ftgo.restaurantservice.domain.MenuItem> restaurantMenuItems =
        Collections.singletonList(new net.chrisrichardson.ftgo.restaurantservice.domain.MenuItem(
            CHICKEN_VINDALOO_MENU_ITEM_ID, CHICKEN_VINDALOO, CHICKEN_VINDALOO_PRICE));
    RestaurantMenu menu = new RestaurantMenu(restaurantMenuItems);
    return new RestaurantCreated(AJANTA_RESTAURANT_NAME, RESTAURANT_ADDRESS, menu);
  }
}

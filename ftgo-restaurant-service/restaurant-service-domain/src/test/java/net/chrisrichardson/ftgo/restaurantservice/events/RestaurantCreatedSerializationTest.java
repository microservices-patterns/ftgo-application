package net.chrisrichardson.ftgo.restaurantservice.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.restaurantservice.domain.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantMenu;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantCreatedSerializationTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setup() {
    CommonJsonMapperInitializer.registerMoneyModule();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new net.chrisrichardson.ftgo.common.MoneyModule());
  }

  public static final String AJANTA_RESTAURANT_NAME = "Ajanta";
  public static final long AJANTA_ID = 1L;
  public static final String CHICKEN_VINDALOO = "Chicken Vindaloo";
  public static final String CHICKEN_VINDALOO_MENU_ITEM_ID = "1";
  public static final Money CHICKEN_VINDALOO_PRICE = new Money("12.34");
  public static final Address RESTAURANT_ADDRESS = new Address("1 Main Street", "Unit 99", "Oakland", "CA", "94611");

  public static MenuItem CHICKEN_VINDALOO_MENU_ITEM = new MenuItem(CHICKEN_VINDALOO_MENU_ITEM_ID, CHICKEN_VINDALOO, CHICKEN_VINDALOO_PRICE);

  @Test
  void shouldSerialize() throws JsonProcessingException {
    RestaurantCreated event = new RestaurantCreated(AJANTA_RESTAURANT_NAME, RESTAURANT_ADDRESS,
            new RestaurantMenu(Collections.singletonList(CHICKEN_VINDALOO_MENU_ITEM)));
    String json = objectMapper.writeValueAsString(event);
    assertNotNull(json);
    assertTrue(json.contains(AJANTA_RESTAURANT_NAME));
    assertTrue(json.contains("Oakland"));
  }

  @Test
  void shouldDeserialize() throws JsonProcessingException {
    RestaurantCreated event = new RestaurantCreated(AJANTA_RESTAURANT_NAME, RESTAURANT_ADDRESS,
            new RestaurantMenu(Collections.singletonList(CHICKEN_VINDALOO_MENU_ITEM)));
    String json = objectMapper.writeValueAsString(event);
    RestaurantCreated deserialized = objectMapper.readValue(json, RestaurantCreated.class);
    assertEquals(AJANTA_RESTAURANT_NAME, deserialized.getName());
  }

}

package net.chrisrichardson.ftgo.restaurantservice.events;

import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.restaurantservice.domain.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.testutil.jsonschema.ValidatingJSONMapper;
import org.json.JSONException;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class RestaurantCreatedSerializationTest {

  static {
    CommonJsonMapperInitializer.registerMoneyModule();
  }

  public static final String AJANTA_RESTAURANT_NAME = "Ajanta";
  public static final long AJANTA_ID = 1L;
  public static final String CHICKEN_VINDALOO = "Chicken Vindaloo";
  public static final String CHICKEN_VINDALOO_MENU_ITEM_ID = "1";
  public static final Money CHICKEN_VINDALOO_PRICE = new Money("12.34");
  public static final Address RESTAURANT_ADDRESS = new Address("1 Main Street", "Unit 99", "Oakland", "CA", "94611");

  public static MenuItem CHICKEN_VINDALOO_MENU_ITEM = new MenuItem(CHICKEN_VINDALOO_MENU_ITEM_ID, CHICKEN_VINDALOO, CHICKEN_VINDALOO_PRICE);

  @Test
  public void shouldSerialize() throws JSONException {

    ValidatingJSONMapper mapper = ValidatingJSONMapper.forSchema("/ftgo-restaurant-service-api-spec/messages/RestaurantCreated.json");

    RestaurantCreated event = new RestaurantCreated(AJANTA_RESTAURANT_NAME, RESTAURANT_ADDRESS,
            new RestaurantMenu(Collections.singletonList(CHICKEN_VINDALOO_MENU_ITEM)));
    String json = mapper.toJSON(event);
    assertNotNull(json);
  }


}
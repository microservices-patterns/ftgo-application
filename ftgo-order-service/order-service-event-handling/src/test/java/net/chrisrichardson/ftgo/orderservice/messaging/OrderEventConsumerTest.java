package net.chrisrichardson.ftgo.orderservice.messaging;

import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.orderservice.RestaurantMother;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.restaurantservice.domain.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.eventuate.tram.testing.DomainEventHandlerUnitTestSupport.given;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OrderEventConsumerTest {

  private OrderService orderService;
  private OrderEventConsumer orderEventConsumer;

  @BeforeEach
  public void setUp() {
    orderService = mock(OrderService.class);
    orderEventConsumer = new OrderEventConsumer(orderService);
  }

  @Test
  public void shouldCreateMenu() {

    CommonJsonMapperInitializer.registerMoneyModule();

    given().
            eventHandlers(orderEventConsumer.domainEventHandlers()).
    when().
            aggregate("net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant", AJANTA_ID).
            publishes(makeAjantaRestaurantCreatedEvent()).
    then().
       verify(() -> {
         verify(orderService).createMenu(AJANTA_ID, AJANTA_RESTAURANT_NAME, AJANTA_RESTAURANT_MENU_ITEMS);
       })
    ;

  }

  private RestaurantCreated makeAjantaRestaurantCreatedEvent() {
    RestaurantMenu menu = new RestaurantMenu(Collections.singletonList(
        new MenuItem(CHICKEN_VINDALOO_MENU_ITEM_ID, CHICKEN_VINDALOO, CHICKEN_VINDALOO_PRICE)));
    return new RestaurantCreated(AJANTA_RESTAURANT_NAME, RESTAURANT_ADDRESS, menu);
  }

}

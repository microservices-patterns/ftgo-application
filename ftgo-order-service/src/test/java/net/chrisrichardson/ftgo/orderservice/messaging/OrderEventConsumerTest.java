package net.chrisrichardson.ftgo.orderservice.messaging;

import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.orderservice.RestaurantMother;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import org.junit.Before;
import org.junit.Test;

import static io.eventuate.tram.testing.DomainEventHandlerUnitTestSupport.given;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.AJANTA_RESTAURANT_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OrderEventConsumerTest {

  private OrderService orderService;
  private OrderEventConsumer orderEventConsumer;

  @Before
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
            publishes(RestaurantMother.makeAjantaRestaurantCreatedEvent()).
    then().
       verify(() -> {
         verify(orderService).createMenu(AJANTA_ID, AJANTA_RESTAURANT_NAME, RestaurantMother.AJANTA_RESTAURANT_MENU_ITEMS);
       })
    ;

  }

}
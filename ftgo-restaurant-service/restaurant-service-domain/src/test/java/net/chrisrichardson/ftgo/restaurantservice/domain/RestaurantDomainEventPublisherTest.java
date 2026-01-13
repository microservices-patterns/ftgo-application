package net.chrisrichardson.ftgo.restaurantservice.domain;

import net.chrisrichardson.ftgo.restaurantservice.api.RestaurantServiceChannels;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantDomainEventPublisherTest {

  @Test
  void verifyRestaurantEventChannel() {
    assertEquals(RestaurantServiceChannels.RESTAURANT_EVENT_CHANNEL, new RestaurantDomainEventPublisher(null).getAggregateType().getName());
  }

}

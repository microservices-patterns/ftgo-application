package net.chrisrichardson.ftgo.restaurantservice.domain;

import net.chrisrichardson.ftgo.restaurantservice.RestaurantServiceChannels;
import org.junit.Test;

import static org.junit.Assert.*;

public class RestaurantDomainEventPublisherTest {

  @Test
  public void verifyRestaurantEventChannel() {
    assertEquals(RestaurantServiceChannels.RESTAURANT_EVENT_CHANNEL, new RestaurantDomainEventPublisher(null).getAggregateType().getName());
  }


}
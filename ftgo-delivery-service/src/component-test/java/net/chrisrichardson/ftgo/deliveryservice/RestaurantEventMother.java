package net.chrisrichardson.ftgo.deliveryservice;

import net.chrisrichardson.ftgo.deliveryservice.domain.DeliveryServiceTestData;
import net.chrisrichardson.ftgo.deliveryservice.messaging.RestaurantEventMapper;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;

public class RestaurantEventMother {
  static RestaurantCreated makeRestaurantCreated() {
    return new RestaurantCreated()
            .withName("Delicious Indian")
            .withAddress(RestaurantEventMapper.fromAddress(DeliveryServiceTestData.PICKUP_ADDRESS)).withMenu(null);
  }
}

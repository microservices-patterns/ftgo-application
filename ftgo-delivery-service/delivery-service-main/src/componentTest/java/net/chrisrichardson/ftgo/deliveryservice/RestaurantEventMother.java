package net.chrisrichardson.ftgo.deliveryservice;

import net.chrisrichardson.ftgo.deliveryservice.domain.DeliveryServiceTestData;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;

public class RestaurantEventMother {

  static RestaurantCreated makeRestaurantCreated() {
    return new RestaurantCreated("Delicious Indian", DeliveryServiceTestData.PICKUP_ADDRESS);
  }
}

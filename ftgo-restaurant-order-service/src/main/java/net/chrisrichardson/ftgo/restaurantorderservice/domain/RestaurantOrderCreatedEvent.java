package net.chrisrichardson.ftgo.restaurantorderservice.domain;


import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;

public class RestaurantOrderCreatedEvent implements RestaurantOrderDomainEvent {
  public RestaurantOrderCreatedEvent(Long id, RestaurantOrderDetails details) {

  }
}

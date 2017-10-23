package net.chrisrichardson.ftgo.restaurantorderservice.domain;


import io.eventuate.tram.events.common.DomainEvent;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;

public class RestaurantOrderCreatedEvent implements DomainEvent {
  public RestaurantOrderCreatedEvent(Long id, RestaurantOrderDetails details) {

  }
}

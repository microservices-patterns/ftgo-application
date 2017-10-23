package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;

import java.time.LocalDateTime;

public class RestaurantOrderAcceptedEvent implements DomainEvent {
  public RestaurantOrderAcceptedEvent(LocalDateTime readyBy) {

  }
}

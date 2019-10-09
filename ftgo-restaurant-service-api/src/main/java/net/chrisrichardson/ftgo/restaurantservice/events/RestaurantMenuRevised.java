package net.chrisrichardson.ftgo.restaurantservice.events;

import io.eventuate.tram.events.common.DomainEvent;

public class RestaurantMenuRevised implements RestaurantDomainEvent {

  private RestaurantMenu menu;

  public RestaurantMenu getRevisedMenu() {
    return menu;
  }
}

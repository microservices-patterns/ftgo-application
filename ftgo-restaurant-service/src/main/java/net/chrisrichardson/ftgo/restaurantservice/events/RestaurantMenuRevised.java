package net.chrisrichardson.ftgo.restaurantservice.events;

import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantMenu;

public class RestaurantMenuRevised implements RestaurantDomainEvent {

  private RestaurantMenu menu;

  public RestaurantMenu getRevisedMenu() {
    return menu;
  }
}

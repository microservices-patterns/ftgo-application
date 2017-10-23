package net.chrisrichardson.ftgo.restaurantservice.events;

import io.eventuate.tram.events.common.DomainEvent;

public class RestaurantCreated implements DomainEvent {
  private String name;
  private RestaurantMenu menu;

  public String getName() {
    return name;
  }

  private RestaurantCreated() {
  }

  public RestaurantCreated(String name, RestaurantMenu menu) {
    this.name = name;
    this.menu = menu;
  }

  public RestaurantMenu getMenu() {
    return menu;
  }

  public void setMenu(RestaurantMenu menu) {
    this.menu = menu;
  }

  public void setName(String name) {
    this.name = name;
  }
}

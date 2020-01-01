package net.chrisrichardson.ftgo.kitchenservice.domain;

import java.util.List;

public class RestaurantMenu {
  private List<MenuItem> menuItems;

  public RestaurantMenu(List<MenuItem> menuItems) {
    this.menuItems = menuItems;
  }

  public List<MenuItem> getMenuItems() {
    return menuItems;
  }
}

package net.chrisrichardson.ftgo.orderservice.messaging;

import net.chrisrichardson.ftgo.orderservice.domain.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantMenu;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantEventMapper {

  public static List<MenuItem> toMenuItems(RestaurantMenu menu) {
    return menu.getMenuItems().stream()
        .map(mi -> new MenuItem(mi.getId(), mi.getName(), mi.getPrice()))
        .collect(Collectors.toList());
  }

}

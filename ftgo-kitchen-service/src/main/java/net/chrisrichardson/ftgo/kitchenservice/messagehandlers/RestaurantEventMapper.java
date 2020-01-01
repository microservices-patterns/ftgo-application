package net.chrisrichardson.ftgo.kitchenservice.messagehandlers;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItem;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantEventMapper {

  public static List<net.chrisrichardson.ftgo.kitchenservice.domain.MenuItem> toMenuItems(List<MenuItem> menuItems) {
    return menuItems.stream().map(mi -> new net.chrisrichardson.ftgo.kitchenservice.domain.MenuItem(mi.getId(), mi.getName(), new Money(mi.getPrice()))).collect(Collectors.toList());
  }

}

package net.chrisrichardson.ftgo.orderservice.messaging;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.restaurantservice.events.Address;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantEventMapper {

  @NotNull
  public static List<MenuItem> fromMenuItems(List<net.chrisrichardson.ftgo.orderservice.domain.MenuItem> menuItems) {
    return menuItems.stream().map(mi -> new MenuItem().withId(mi.getId()).withName(mi.getName()).withPrice(mi.getPrice().asString())).collect(Collectors.toList());
  }

  public static Address fromAddress(net.chrisrichardson.ftgo.common.Address a) {
    return new Address().withStreet1(a.getStreet1()).withStreet2(a.getStreet2()).withCity(a.getCity()).withZip(a.getZip());
  }

  public static List<net.chrisrichardson.ftgo.orderservice.domain.MenuItem> toMenuItems(List<MenuItem> menuItems) {
    return menuItems.stream().map(mi -> new net.chrisrichardson.ftgo.orderservice.domain.MenuItem(mi.getId(), mi.getName(), new Money(mi.getPrice()))).collect(Collectors.toList());
  }

}

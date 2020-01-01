package net.chrisrichardson.ftgo.deliveryservice.messaging;

import net.chrisrichardson.ftgo.common.Address;

public class RestaurantEventMapper {

  public static Address toAddress(net.chrisrichardson.ftgo.restaurantservice.events.Address address) {
    return new Address(address.getStreet1(), address.getStreet2(), address.getCity(), address.getState(), address.getZip());
  }

  public static net.chrisrichardson.ftgo.restaurantservice.events.Address fromAddress(net.chrisrichardson.ftgo.common.Address a) {
    return new net.chrisrichardson.ftgo.restaurantservice.events.Address().withStreet1(a.getStreet1()).withStreet2(a.getStreet2()).withCity(a.getCity()).withZip(a.getZip());
  }

}

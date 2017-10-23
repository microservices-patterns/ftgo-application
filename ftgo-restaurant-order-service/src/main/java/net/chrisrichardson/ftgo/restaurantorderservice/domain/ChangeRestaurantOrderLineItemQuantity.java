package net.chrisrichardson.ftgo.restaurantorderservice.domain;


import io.eventuate.tram.commands.common.Command;

public class ChangeRestaurantOrderLineItemQuantity implements Command {
  public ChangeRestaurantOrderLineItemQuantity(Long orderId) {
  }
}

package net.chrisrichardson.ftgo.restaurantorderservice.api;


import io.eventuate.tram.commands.common.Command;

public class ChangeRestaurantOrderLineItemQuantity implements Command {
  public ChangeRestaurantOrderLineItemQuantity(Long orderId) {
  }
}

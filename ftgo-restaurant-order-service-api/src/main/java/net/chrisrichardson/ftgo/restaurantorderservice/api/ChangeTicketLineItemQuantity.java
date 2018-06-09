package net.chrisrichardson.ftgo.restaurantorderservice.api;


import io.eventuate.tram.commands.common.Command;

public class ChangeTicketLineItemQuantity implements Command {
  public ChangeTicketLineItemQuantity(Long orderId) {
  }
}

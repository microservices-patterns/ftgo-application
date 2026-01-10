package net.chrisrichardson.ftgo.kitchenservice.api;


import io.eventuate.tram.commands.common.Command;

public class ChangeTicketLineItemQuantity implements Command {
  public ChangeTicketLineItemQuantity(Long orderId) {
  }
}

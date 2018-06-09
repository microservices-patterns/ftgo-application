package net.chrisrichardson.ftgo.restaurantorderservice.domain;

public class TicketNotFoundException extends RuntimeException {
  public TicketNotFoundException(long orderId) {
    super("Ticket not found: " + orderId);
  }
}

package net.chrisrichardson.ftgo.kitchenservice.domain;

public class TicketNotFoundException extends RuntimeException {
  public TicketNotFoundException(long orderId) {
    super("Ticket not found: " + orderId);
  }
}

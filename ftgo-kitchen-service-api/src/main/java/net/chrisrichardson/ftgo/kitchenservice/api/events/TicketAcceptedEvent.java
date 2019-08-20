package net.chrisrichardson.ftgo.kitchenservice.api.events;

import java.time.LocalDateTime;

public class TicketAcceptedEvent implements TicketDomainEvent {
  public TicketAcceptedEvent(LocalDateTime readyBy) {

  }
}

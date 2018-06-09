package net.chrisrichardson.ftgo.kitchenservice.domain;

import java.time.LocalDateTime;

public class TicketAcceptedEvent implements TicketDomainEvent {
  public TicketAcceptedEvent(LocalDateTime readyBy) {

  }
}

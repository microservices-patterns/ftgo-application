package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import java.time.LocalDateTime;

public class TicketAcceptedEvent implements TicketDomainEvent {
  public TicketAcceptedEvent(LocalDateTime readyBy) {

  }
}

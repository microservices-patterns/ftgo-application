package net.chrisrichardson.ftgo.kitchenservice.domain;


import net.chrisrichardson.ftgo.kitchenservice.api.TicketDetails;

public class TicketCreatedEvent implements TicketDomainEvent {
  public TicketCreatedEvent(Long id, TicketDetails details) {

  }
}

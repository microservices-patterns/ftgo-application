package net.chrisrichardson.ftgo.restaurantorderservice.domain;


import net.chrisrichardson.ftgo.restaurantorderservice.api.TicketDetails;

public class TicketCreatedEvent implements TicketDomainEvent {
  public TicketCreatedEvent(Long id, TicketDetails details) {

  }
}

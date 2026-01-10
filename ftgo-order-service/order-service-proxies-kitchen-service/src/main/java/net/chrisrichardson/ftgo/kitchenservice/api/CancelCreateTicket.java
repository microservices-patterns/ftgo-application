package net.chrisrichardson.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

public class CancelCreateTicket implements Command {
  private Long ticketId;

  private CancelCreateTicket() {
  }

  public CancelCreateTicket(long ticketId) {
    this.ticketId = ticketId;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }
}

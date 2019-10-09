package net.chrisrichardson.ftgo.kitchenservice.domain;

import net.chrisrichardson.ftgo.kitchenservice.api.KitchenServiceChannels;
import org.junit.Test;

import static org.junit.Assert.*;

public class TicketDomainEventPublisherTest {

  @Test
  public void verifyTicketEventChannel() {
    assertEquals(KitchenServiceChannels.TICKET_EVENT_CHANNEL, new TicketDomainEventPublisher(null).getAggregateType().getName());
  }


}
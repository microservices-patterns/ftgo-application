package net.chrisrichardson.ftgo.kitchenservice.domain;

import net.chrisrichardson.ftgo.kitchenservice.api.KitchenServiceChannels;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketDomainEventPublisherTest {

  @Test
  void verifyTicketEventChannel() {
    assertEquals(KitchenServiceChannels.TICKET_EVENT_CHANNEL, new TicketDomainEventPublisher(null).getAggregateType().getName());
  }

}

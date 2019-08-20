package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderDomainEventPublisherTest {

  @Test
  public void verifyOrderEventChannel() {
    assertEquals(OrderServiceChannels.ORDER_EVENT_CHANNEL, new OrderDomainEventPublisher(null).getAggregateType().getName());
  }

}
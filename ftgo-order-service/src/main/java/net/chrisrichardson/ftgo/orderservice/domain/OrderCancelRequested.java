package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderState;

public class OrderCancelRequested implements DomainEvent {
  private OrderState state;

  public OrderCancelRequested(OrderState state) {

    this.state = state;
  }

  public OrderState getState() {
    return state;
  }
}

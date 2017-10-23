package net.chrisrichardson.ftgo.orderservice.api.events;

import io.eventuate.tram.events.common.DomainEvent;

public class OrderCreatedEvent implements DomainEvent {
  private OrderDetails orderDetails;
  private OrderState orderState;

  private OrderCreatedEvent() {
  }

  public void setOrderDetails(OrderDetails orderDetails) {
    this.orderDetails = orderDetails;
  }

  public void setOrderState(OrderState orderState) {
    this.orderState = orderState;
  }

  public OrderCreatedEvent(OrderState orderState, OrderDetails orderDetails) {
    this.orderState = orderState;

    this.orderDetails = orderDetails;
  }

  public OrderDetails getOrderDetails() {
    return orderDetails;
  }

  public OrderState getOrderState() {
    return orderState;
  }
}

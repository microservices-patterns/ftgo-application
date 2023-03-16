package net.chrisrichardson.ftgo.consumerservice.domain.swagger;

import net.chrisrichardson.ftgo.common.Money;

public class GetOrderResponseNew {
  private long orderId;
  private OrderState state;
  private Money orderTotal;

  private GetOrderResponseNew() {
  }

  public GetOrderResponseNew(long orderId, OrderState state, Money orderTotal) {
    this.orderId = orderId;
    this.state = state;
    this.orderTotal = orderTotal;
  }

  public Money getOrderTotal() {
    return orderTotal;
  }

  public void setOrderTotal(Money orderTotal) {
    this.orderTotal = orderTotal;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public OrderState getState() {
    return state;
  }

  public void setState(OrderState state) {
    this.state = state;
  }
}

package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;

public class CreateOrderSagaData  {

  private Long orderId;

  private OrderDetails orderDetails;
  private long restaurantOrderId;

  public Long getOrderId() {
    return orderId;
  }

  private CreateOrderSagaData() {
  }

  public CreateOrderSagaData(Long orderId, OrderDetails orderDetails) {
    this.orderId = orderId;
    this.orderDetails = orderDetails;
  }

  public OrderDetails getOrderDetails() {
    return orderDetails;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public void setRestaurantOrderId(long restaurantOrderId) {
    this.restaurantOrderId = restaurantOrderId;
  }

  public long getRestaurantOrderId() {
    return restaurantOrderId;
  }
}

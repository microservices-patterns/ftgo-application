package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
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

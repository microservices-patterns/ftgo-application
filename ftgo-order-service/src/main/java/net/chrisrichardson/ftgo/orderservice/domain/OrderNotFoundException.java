package net.chrisrichardson.ftgo.orderservice.domain;

public class OrderNotFoundException extends RuntimeException {
  public OrderNotFoundException(Long orderId) {
    super("Order not found" + orderId);
  }
}

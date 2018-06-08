package net.chrisrichardson.ftgo.restaurantorderservice.domain;

public class RestaurantOrderNotFoundException extends RuntimeException {
  public RestaurantOrderNotFoundException(long orderId) {
    super("RestaurantOrder not found: " + orderId);
  }
}

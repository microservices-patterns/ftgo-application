package net.chrisrichardson.ftgo.restaurantorderservice.api;

import io.eventuate.tram.commands.CommandDestination;
import io.eventuate.tram.commands.common.Command;

@CommandDestination("restaurantService")
public class CreateRestaurantOrder implements Command {

  private Long orderId;
  private RestaurantOrderDetails orderDetails;
  private long restaurantId;

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public RestaurantOrderDetails getRestaurantOrderDetails() {
    return orderDetails;
  }

  public void setRestaurantOrderDetails(RestaurantOrderDetails orderDetails) {
    this.orderDetails = orderDetails;
  }

  public CreateRestaurantOrder() {

  }

  public CreateRestaurantOrder(long restaurantId, long orderId, RestaurantOrderDetails orderDetails) {
    this.restaurantId = restaurantId;
    this.orderId = orderId;
    this.orderDetails = orderDetails;
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(long restaurantId) {
    this.restaurantId = restaurantId;
  }
}

package net.chrisrichardson.ftgo.restaurantorderservice.api;

import io.eventuate.tram.commands.common.Command;

public class CancelCreateRestaurantOrder implements Command {
  private Long restaurantOrderId;

  private CancelCreateRestaurantOrder() {
  }

  public CancelCreateRestaurantOrder(long restaurantOrderId) {
    this.restaurantOrderId = restaurantOrderId;
  }

  public Long getRestaurantOrderId() {
    return restaurantOrderId;
  }

  public void setRestaurantOrderId(Long restaurantOrderId) {
    this.restaurantOrderId = restaurantOrderId;
  }
}

package net.chrisrichardson.ftgo.restaurantorderservice.api;

import io.eventuate.tram.commands.common.Command;

public class ConfirmCreateRestaurantOrder implements Command {
  private Long restaurantOrderId;

  private ConfirmCreateRestaurantOrder() {
  }


  public ConfirmCreateRestaurantOrder(Long restaurantOrderId) {
    this.restaurantOrderId = restaurantOrderId;
  }

  public Long getRestaurantOrderId() {
    return restaurantOrderId;
  }

  public void setRestaurantOrderId(Long restaurantOrderId) {
    this.restaurantOrderId = restaurantOrderId;
  }
}

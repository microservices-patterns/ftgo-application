package net.chrisrichardson.ftgo.restaurantorderservice.api;

import io.eventuate.tram.commands.common.Command;

public class UndoBeginReviseRestaurantOrderCommand implements Command {
  private long restaurantId;
  private Long orderId;

  public UndoBeginReviseRestaurantOrderCommand() {
  }

  public UndoBeginReviseRestaurantOrderCommand(long restaurantId, Long orderId) {

    this.restaurantId = restaurantId;
    this.orderId = orderId;
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(long restaurantId) {
    this.restaurantId = restaurantId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }
}

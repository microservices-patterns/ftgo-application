package net.chrisrichardson.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

public class UndoBeginReviseTicketCommand implements Command {
  private long restaurantId;
  private Long orderId;

  public UndoBeginReviseTicketCommand() {
  }

  public UndoBeginReviseTicketCommand(long restaurantId, Long orderId) {

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

package net.chrisrichardson.ftgo.restaurantorderservice.api;

import io.eventuate.tram.commands.common.Command;

import java.util.Map;

public class ConfirmReviseRestaurantOrderCommand implements Command {
  private long restaurantId;
  private long orderId;
  private Map<String, Integer> revisedLineItemQuantities;

  private ConfirmReviseRestaurantOrderCommand() {
  }

  public ConfirmReviseRestaurantOrderCommand(long restaurantId, Long orderId, Map<String, Integer> revisedLineItemQuantities) {

    this.restaurantId = restaurantId;
    this.orderId = orderId;
    this.revisedLineItemQuantities = revisedLineItemQuantities;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(long restaurantId) {
    this.restaurantId = restaurantId;
  }

  public Map<String, Integer> getRevisedLineItemQuantities() {
    return revisedLineItemQuantities;
  }

  public void setRevisedLineItemQuantities(Map<String, Integer> revisedLineItemQuantities) {
    this.revisedLineItemQuantities = revisedLineItemQuantities;
  }
}

package net.chrisrichardson.ftgo.restaurantorderservice.api;

import io.eventuate.tram.commands.common.Command;

import java.util.Map;

public class BeginReviseRestaurantOrderCommand implements Command {
  private long restaurantId;
  private Long orderId;
  private Map<String, Integer> revisedLineItemQuantities;

  private BeginReviseRestaurantOrderCommand() {
  }

  public BeginReviseRestaurantOrderCommand(long restaurantId, Long orderId, Map<String, Integer> revisedLineItemQuantities) {
    this.restaurantId = restaurantId;
    this.orderId = orderId;
    this.revisedLineItemQuantities = revisedLineItemQuantities;
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

  public Map<String, Integer> getRevisedLineItemQuantities() {
    return revisedLineItemQuantities;
  }

  public void setRevisedLineItemQuantities(Map<String, Integer> revisedLineItemQuantities) {
    this.revisedLineItemQuantities = revisedLineItemQuantities;
  }
}

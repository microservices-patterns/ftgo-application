package net.chrisrichardson.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

import java.util.Map;

public class BeginReviseTicketCommand implements Command {
  private long restaurantId;
  private Long orderId;
  private Map<String, Integer> revisedLineItemQuantities;

  private BeginReviseTicketCommand() {
  }

  public BeginReviseTicketCommand(long restaurantId, Long orderId, Map<String, Integer> revisedLineItemQuantities) {
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

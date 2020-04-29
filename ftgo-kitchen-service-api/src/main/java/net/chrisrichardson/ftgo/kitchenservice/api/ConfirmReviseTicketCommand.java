package net.chrisrichardson.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;
import net.chrisrichardson.ftgo.common.RevisedOrderLineItem;

import java.util.Map;

public class ConfirmReviseTicketCommand implements Command {
  private long restaurantId;
  private long orderId;
  private RevisedOrderLineItem[] revisedOrderLineItems;

  private ConfirmReviseTicketCommand() {
  }

  public ConfirmReviseTicketCommand(long restaurantId, Long orderId, RevisedOrderLineItem[] revisedOrderLineItems) {

    this.restaurantId = restaurantId;
    this.orderId = orderId;
    this.revisedOrderLineItems = revisedOrderLineItems;
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

  public RevisedOrderLineItem[] getRevisedOrderLineItems() {
    return revisedOrderLineItems;
  }

  public void setRevisedOrderLineItems(RevisedOrderLineItem[] revisedOrderLineItems) {
    this.revisedOrderLineItems = revisedOrderLineItems;
  }
}

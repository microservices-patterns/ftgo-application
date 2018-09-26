package net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderState;
import org.joda.time.DateTime;

import java.util.List;

public class Order {
  private String consumerId;
  private DateTime creationDate = DateTime.now();
  private OrderState status;
  private String orderId;
  private List<OrderLineItem> lineItems;
  private Money orderTotal;
  private long restaurantId;
  private String restaurantName;

  public Order(String orderId, String consumerId, OrderState status, List<OrderLineItem> lineItems, Money orderTotal, long restaurantId, String restaurantName) {
    this.orderId = orderId;
    this.consumerId = consumerId;
    this.status = status;
    this.lineItems = lineItems;
    this.orderTotal = orderTotal;
    this.restaurantId = restaurantId;
    this.restaurantName = restaurantName;
  }

  public String getRestaurantName() {
    return restaurantName;
  }

  public String getOrderId() {
    return orderId;
  }

  public long getRestaurantId() {
    return restaurantId;
  }
  
  public List<OrderLineItem> getLineItems() {
    return lineItems;
  }

  public Money getOrderTotal() {
    return orderTotal;
  }

  public void setCreationDate(DateTime creationDate) {
    this.creationDate = creationDate;
  }

  public String getConsumerId() {
    return consumerId;
  }

  public DateTime getCreationDate() {
    return creationDate;
  }

  public OrderState getStatus() {
    return status;
  }


}

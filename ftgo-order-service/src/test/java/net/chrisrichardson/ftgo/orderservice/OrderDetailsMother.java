package net.chrisrichardson.ftgo.orderservice;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;

import java.util.Collections;

public class OrderDetailsMother {
  static OrderDetails makeOrderDetails(long consumerId, long restaurantId, Money orderTotal) {
    return new OrderDetails(consumerId, restaurantId, Collections.singletonList(new OrderLineItem("samosas", "Samosas", new Money("2.50"), 3)), orderTotal);
  }
}

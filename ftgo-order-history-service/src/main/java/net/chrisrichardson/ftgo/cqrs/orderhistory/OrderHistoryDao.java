package net.chrisrichardson.ftgo.cqrs.orderhistory;


import net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb.SourceEvent;
import net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb.Order;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderState;

import java.util.Optional;

public interface OrderHistoryDao {

  boolean addOrder(Order order, Optional<SourceEvent> eventSource);

  OrderHistory findOrderHistory(String consumerId, OrderHistoryFilter filter);

  boolean updateOrderState(String orderId, OrderState newState, Optional<SourceEvent> eventSource);

  void noteTicketPreparationStarted(String orderId);

  void noteTicketPreparationCompleted(String orderId);

  void notePickedUp(String orderId, Optional<SourceEvent> eventSource);

  void updateLocation(String orderId, Location location);

  void noteDelivered(String orderId);

  Optional<Order> findOrder(String orderId);

}

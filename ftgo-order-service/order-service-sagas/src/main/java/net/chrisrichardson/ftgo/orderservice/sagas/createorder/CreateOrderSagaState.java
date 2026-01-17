package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.domain.DeliveryInformation;
import net.chrisrichardson.ftgo.orderservice.domain.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CreateOrderSagaState {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private Long orderId;
  private OrderDetails orderDetails;
  private long ticketId;

  // Order creation parameters (used before Order is created)
  private long consumerId;
  private long restaurantId;
  private DeliveryInformation deliveryInformation;
  private List<MenuItemIdAndQuantity> lineItems;

  public Long getOrderId() {
    return orderId;
  }

  private CreateOrderSagaState() {
  }

  public CreateOrderSagaState(long consumerId, long restaurantId,
                              DeliveryInformation deliveryInformation,
                              List<MenuItemIdAndQuantity> lineItems) {
    this.consumerId = consumerId;
    this.restaurantId = restaurantId;
    this.deliveryInformation = deliveryInformation;
    this.lineItems = lineItems;
  }

  public long getConsumerId() {
    return consumerId;
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public DeliveryInformation getDeliveryInformation() {
    return deliveryInformation;
  }

  public List<MenuItemIdAndQuantity> getLineItems() {
    return lineItems;
  }

  public OrderDetails getOrderDetails() {
    return orderDetails;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public void setOrderDetails(OrderDetails orderDetails) {
    this.orderDetails = orderDetails;
  }

  public void setTicketId(long ticketId) {
    this.ticketId = ticketId;
  }

  public long getTicketId() {
    return ticketId;
  }

  void handleCreateTicketReply(CreateTicketReply reply) {
    logger.debug("getTicketId {}", reply.getTicketId());
    setTicketId(reply.getTicketId());
  }
}

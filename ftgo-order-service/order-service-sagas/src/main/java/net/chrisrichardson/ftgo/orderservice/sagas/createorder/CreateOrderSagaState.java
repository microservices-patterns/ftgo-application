package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.domain.DeliveryInformation;
import net.chrisrichardson.ftgo.orderservice.domain.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.kitchenservice.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

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

  CreateTicket makeCreateTicketCommand() {
    return new CreateTicket(getOrderDetails().getRestaurantId(), getOrderId(), makeTicketDetails(getOrderDetails()));
  }

  private TicketDetails makeTicketDetails(OrderDetails orderDetails) {
    return new TicketDetails(makeTicketLineItems(orderDetails.getLineItems()));
  }

  private List<TicketLineItem> makeTicketLineItems(List<OrderLineItem> lineItems) {
    return lineItems.stream().map(this::makeTicketLineItem).collect(toList());
  }

  private TicketLineItem makeTicketLineItem(OrderLineItem orderLineItem) {
    return new TicketLineItem(orderLineItem.getMenuItemId(), orderLineItem.getName(), orderLineItem.getQuantity());
  }

  void handleCreateTicketReply(CreateTicketReply reply) {
    logger.debug("getTicketId {}", reply.getTicketId());
    setTicketId(reply.getTicketId());
  }

  CancelCreateTicket makeCancelCreateTicketCommand() {
    return new CancelCreateTicket(getOrderId());
  }

  ValidateOrderByConsumer makeValidateOrderByConsumerCommand() {
    return new ValidateOrderByConsumer(
            getOrderDetails().getConsumerId(),
            getOrderId(),
            getOrderDetails().getOrderTotal());
  }

  AuthorizeCommand makeAuthorizeCommand() {
    return new AuthorizeCommand(
            getOrderDetails().getConsumerId(),
            getOrderId(),
            getOrderDetails().getOrderTotal());
  }

  ConfirmCreateTicket makeConfirmCreateTicketCommand() {
    return new ConfirmCreateTicket(getTicketId());
  }
}

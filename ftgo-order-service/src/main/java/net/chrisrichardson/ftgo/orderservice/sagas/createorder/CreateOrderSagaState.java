package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ApproveOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.RejectOrderCommand;
import net.chrisrichardson.ftgo.restaurantorderservice.api.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CreateOrderSagaState {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private Long orderId;

  private OrderDetails orderDetails;
  private long restaurantOrderId;

  public Long getOrderId() {
    return orderId;
  }

  private CreateOrderSagaState() {
  }

  public CreateOrderSagaState(Long orderId, OrderDetails orderDetails) {
    this.orderId = orderId;
    this.orderDetails = orderDetails;
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public OrderDetails getOrderDetails() {
    return orderDetails;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public void setRestaurantOrderId(long restaurantOrderId) {
    this.restaurantOrderId = restaurantOrderId;
  }

  public long getRestaurantOrderId() {
    return restaurantOrderId;
  }

  CreateRestaurantOrder makeCreateRestaurantOrderCommand() {
    return new CreateRestaurantOrder(getOrderDetails().getRestaurantId(), getOrderId(), makeRestaurantOrderDetails(getOrderDetails()));
  }

  private RestaurantOrderDetails makeRestaurantOrderDetails(OrderDetails orderDetails) {
    // TODO FIXME
    return new RestaurantOrderDetails(makeRestaurantOrderLineItems(orderDetails.getLineItems()));
  }

  private List<RestaurantOrderLineItem> makeRestaurantOrderLineItems(List<OrderLineItem> lineItems) {
    return lineItems.stream().map(this::makeRestaurantOrderLineItem).collect(toList());
  }

  private RestaurantOrderLineItem makeRestaurantOrderLineItem(OrderLineItem orderLineItem) {
    return new RestaurantOrderLineItem(orderLineItem.getMenuItemId(), orderLineItem.getName(), orderLineItem.getQuantity());
  }

  void handleCreateRestaurantOrderReply(CreateRestaurantOrderReply reply) {
    logger.debug("getRestaurantOrderId {}", reply.getRestaurantOrderId());
    setRestaurantOrderId(reply.getRestaurantOrderId());
  }

  CancelCreateRestaurantOrder makeCancelCreateRestaurantOrderCommand() {
    return new CancelCreateRestaurantOrder(getOrderId());
  }

  RejectOrderCommand makeRejectOrderCommand() {
    return new RejectOrderCommand(getOrderId());
  }

  ValidateOrderByConsumer makeValidateOrderByConsumerCommand() {
    return new ValidateOrderByConsumer(getOrderDetails().getConsumerId(), getOrderId(), getOrderDetails().getOrderTotal());
  }

  AuthorizeCommand makeAuthorizeCommand() {
    return new AuthorizeCommand(getOrderDetails().getConsumerId(), getOrderId(), getOrderDetails().getOrderTotal());
  }

  ApproveOrderCommand makeApproveOrderCommand() {
    return new ApproveOrderCommand(getOrderId());
  }

  ConfirmCreateRestaurantOrder makeConfirmCreateRestaurantOrderCommand() {
    return new ConfirmCreateRestaurantOrder(getRestaurantOrderId());

  }
}

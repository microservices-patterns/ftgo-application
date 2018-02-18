package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.*;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CancelCreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.ConfirmCreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrderReply;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderLineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {


  private Logger logger = LoggerFactory.getLogger(getClass());

  private SagaDefinition<CreateOrderSagaData> sagaDefinition;

  public CreateOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService, RestaurantOrderServiceProxy restaurantOrderService,
                         AccountingServiceProxy accountingService) {
    this.sagaDefinition =
             step()
              .withCompensation(orderService.reject, this::makeRejectOrderCommand)
            .step()
              .invokeParticipant(consumerService.validateOrder, this::makeValidateOrderByConsumer)
            .step()
              .invokeParticipant(restaurantOrderService.create, this::makeCreateRestaurantOrderCommand)
              .onReply(CreateRestaurantOrderReply.class, this::handleCreateRestaurantOrderReply)
              .withCompensation(restaurantOrderService.cancel, this::makeCancelCreateRestaurantOrder)
            .step()
              .invokeParticipant(accountingService.authorize, this::makeAuthorizeCommand)
            .step()
              .invokeParticipant(restaurantOrderService.confirmCreate, this::makeConfirmCreateRestaurantOrder)
            .step()
              .invokeParticipant(orderService.approve, this::makeApproveOrderCommand)
            .build();

  }


  @Override
  public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
    return sagaDefinition;
  }

  private void handleCreateRestaurantOrderReply(CreateOrderSagaData data,
                                                CreateRestaurantOrderReply reply) {
    logger.debug("getRestaurantOrderId {}", reply.getRestaurantOrderId());
    data.setRestaurantOrderId(reply.getRestaurantOrderId());
  }

  private AuthorizeCommand makeAuthorizeCommand(CreateOrderSagaData data) {
    return new AuthorizeCommand(data.getOrderDetails().getConsumerId(), data.getOrderId(), data.getOrderDetails().getOrderTotal());
  }

  private CancelCreateRestaurantOrder makeCancelCreateRestaurantOrder(CreateOrderSagaData data) {
    return new CancelCreateRestaurantOrder(data.getOrderId());
  }

  private ValidateOrderByConsumer makeValidateOrderByConsumer(CreateOrderSagaData data) {
    return new ValidateOrderByConsumer(data.getOrderDetails().getConsumerId(), data.getOrderId(), data.getOrderDetails().getOrderTotal());
  }

  private ApproveOrderCommand makeApproveOrderCommand(CreateOrderSagaData data) {
    return new ApproveOrderCommand(data.getOrderId());
  }

  private RejectOrderCommand makeRejectOrderCommand(CreateOrderSagaData data) {
    return new RejectOrderCommand(data.getOrderId());
  }

  private ConfirmCreateRestaurantOrder makeConfirmCreateRestaurantOrder(CreateOrderSagaData data) {
    return new ConfirmCreateRestaurantOrder(data.getRestaurantOrderId());

  }

  private CreateRestaurantOrder makeCreateRestaurantOrderCommand(CreateOrderSagaData data) {
    return new CreateRestaurantOrder(data.getOrderDetails().getRestaurantId(), data.getOrderId(), makeRestaurantOrderDetails(data.getOrderDetails()));
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


}

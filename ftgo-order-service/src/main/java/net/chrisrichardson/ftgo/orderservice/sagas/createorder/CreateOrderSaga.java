package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.ftgo.accountservice.api.AccountingServiceChannels;
import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceChannels;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ApproveOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.RejectOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.RestaurantOrderServiceProxy;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CancelCreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.ConfirmCreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrderReply;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderLineItem;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderServiceChannels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;
import static java.util.stream.Collectors.toList;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {


  private Logger logger = LoggerFactory.getLogger(getClass());

  private SagaDefinition<CreateOrderSagaData> sagaDefinition;

  public CreateOrderSaga(RestaurantOrderServiceProxy restaurantOrderService) {
    this.sagaDefinition = step()
            .withCompensation(this::rejectOrder)
            .step()
            .invokeParticipant(this::verifyConsumer)
            .step()
            .invokeParticipant(restaurantOrderService.create, this::makeCreateRestaurantOrderCommand)
            .onReply(CreateRestaurantOrderReply.class,
                    this::handleCreateRestaurantOrderReply)
            .withCompensation(this::rejectRestaurantOrder)
            .step()
            .invokeParticipant(this::authorizeCard)
            .step()
            .invokeParticipant(restaurantOrderService.confirmCreate, this::confirmCreateRestaurantOrder)
            .step()
            .invokeParticipant(this::approveOrder)
            .build();

  }

  @Override
  public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
    return sagaDefinition;
  }

  private ConfirmCreateRestaurantOrder confirmCreateRestaurantOrder(CreateOrderSagaData data) {
    return new ConfirmCreateRestaurantOrder(data.getRestaurantOrderId());

  }

  private CommandWithDestination approveOrder(CreateOrderSagaData data) {
    return send(new ApproveOrderCommand(data.getOrderId()))
            .to(OrderServiceChannels.orderServiceChannel)
            .build();
  }

  private CommandWithDestination authorizeCard(CreateOrderSagaData data) {
    return send(new AuthorizeCommand(data.getOrderDetails().getConsumerId(), data.getOrderId(), data.getOrderDetails().getOrderTotal()))
            .to(AccountingServiceChannels.accountingServiceChannel)
            .build();

  }

  private CommandWithDestination rejectRestaurantOrder(CreateOrderSagaData data) {
    return send(new CancelCreateRestaurantOrder(data.getOrderId()))
            .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
            .build();

  }

  private void handleCreateRestaurantOrderReply(CreateOrderSagaData data,
                                                CreateRestaurantOrderReply reply) {
    logger.debug("getRestaurantOrderId {}", reply.getRestaurantOrderId());
    data.setRestaurantOrderId(reply.getRestaurantOrderId());
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

  private CommandWithDestination verifyConsumer(CreateOrderSagaData data) {
    return send(new ValidateOrderByConsumer(data.getOrderDetails().getConsumerId(), data.getOrderId(), data.getOrderDetails().getOrderTotal()))
            .to(ConsumerServiceChannels.consumerServiceChannel)
            .build();

  }

  private CommandWithDestination rejectOrder(CreateOrderSagaData data) {
    return send(new RejectOrderCommand(data.getOrderId()))
            .to(OrderServiceChannels.orderServiceChannel)
            .build();

  }


}

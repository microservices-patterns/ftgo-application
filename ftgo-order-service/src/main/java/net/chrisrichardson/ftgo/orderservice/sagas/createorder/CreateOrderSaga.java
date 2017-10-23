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
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ApproveOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.RejectOrderCommand;
import net.chrisrichardson.ftgo.restaurantorderservice.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {


  private Logger logger = LoggerFactory.getLogger(getClass());

  private SagaDefinition<CreateOrderSagaData> sagaDefinition =
          step()
                  .withCompensation(this::rejectOrder)
                  .step()
                  .invokeParticipant(this::verifyConsumer)
                  .step()
                  .invokeParticipant(this::createRestaurantOrder)
                  .onReply(CreateRestaurantOrderReply.class,
                          this::handleCreateRestaurantOrderReply)
                  .withCompensation(this::rejectRestaurantOrder)
                  .step()
                  .invokeParticipant(this::authorizeCard)
                  .step()
                  .invokeParticipant(this::approveOrder)
                  .step()
                  .invokeParticipant(this::approveRestaurantOrder)
                  .build();

  @Override
  public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
    return sagaDefinition;
  }

  private CommandWithDestination approveRestaurantOrder(CreateOrderSagaData data) {
    return send(new ConfirmCreateRestaurantOrder(data.getRestaurantOrderId()))
            .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
            .build();

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

  private CommandWithDestination createRestaurantOrder(CreateOrderSagaData data) {
    return send(new CreateRestaurantOrder(data.getOrderDetails().getRestaurantId(), data.getOrderId(), makeRestaurantOrderDetails(data.getOrderDetails())))
            .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
            .build();
  }

  private RestaurantOrderDetails makeRestaurantOrderDetails(OrderDetails orderDetails) {
    // TODO FIXME
    return new RestaurantOrderDetails();
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

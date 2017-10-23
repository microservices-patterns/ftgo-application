package net.chrisrichardson.ftgo.orderservice.sagas.reviseorder;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.ftgo.accountservice.api.AccountingServiceChannels;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.BeginReviseOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.BeginReviseOrderReply;
import net.chrisrichardson.ftgo.restaurantorderservice.api.BeginReviseRestaurantOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ConfirmReviseOrderCommand;
import net.chrisrichardson.ftgo.accountservice.api.ReviseAuthorization;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.UndoBeginReviseOrderCommand;
import net.chrisrichardson.ftgo.restaurantorderservice.api.ConfirmReviseRestaurantOrderCommand;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderServiceChannels;
import net.chrisrichardson.ftgo.restaurantorderservice.api.UndoBeginReviseRestaurantOrderCommand;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class ReviseOrderSaga implements SimpleSaga<ReviseOrderSagaData> {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private SagaDefinition<ReviseOrderSagaData> sagaDefinition;

  @PostConstruct
  public void initializeSagaDefinition() {
    sagaDefinition = step()
            .invokeParticipant(this::beginReviseOrder)
            .onReply(BeginReviseOrderReply.class, this::handleBeginReviseOrderReply)
            .withCompensation(this::undoBeginReviseOrder)
            .step()
            .invokeParticipant(this::beginReviseRestaurantOrder)
            .withCompensation(this::undoBeginReviseRestaurantOrder)
            .step()
            .invokeParticipant(this::reviseAuthorization)
            .step()
            .invokeParticipant(this::confirmRestaurantOrderRevision)
            .step()
            .invokeParticipant(this::confirmOrderRevision)
            .build();
  }

  private void handleBeginReviseOrderReply(ReviseOrderSagaData data, BeginReviseOrderReply reply) {
    logger.info("ƒ order total: {}", reply.getRevisedOrderTotal());
    data.setRevisedOrderTotal(reply.getRevisedOrderTotal());
  }

  @Override
  public SagaDefinition<ReviseOrderSagaData> getSagaDefinition() {
    return sagaDefinition;
  }

  private CommandWithDestination confirmOrderRevision(ReviseOrderSagaData data) {
    return send(new ConfirmReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
            .to(OrderServiceChannels.orderServiceChannel)
            .build();

  }

  private CommandWithDestination confirmRestaurantOrderRevision(ReviseOrderSagaData data) {
    return send(new ConfirmReviseRestaurantOrderCommand(data.getRestaurantId(), data.getOrderId(), data.getOrderRevision().getRevisedLineItemQuantities()))
            .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
            .build();

  }

  private CommandWithDestination reviseAuthorization(ReviseOrderSagaData data) {
    return send(new ReviseAuthorization(data.getConsumerId(), data.getOrderId(), data.getRevisedOrderTotal()))
            .to(AccountingServiceChannels.accountingServiceChannel)
            .build();

  }

  private CommandWithDestination undoBeginReviseRestaurantOrder(ReviseOrderSagaData data) {
    return send(new UndoBeginReviseRestaurantOrderCommand(data.getRestaurantId(), data.getOrderId()))
            .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
            .build();

  }

  private CommandWithDestination beginReviseRestaurantOrder(ReviseOrderSagaData data) {
    return send(new BeginReviseRestaurantOrderCommand(data.getRestaurantId(), data.getOrderId(), data.getOrderRevision().getRevisedLineItemQuantities()))
            .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
            .build();

  }

  private CommandWithDestination undoBeginReviseOrder(ReviseOrderSagaData data) {
    return send(new UndoBeginReviseOrderCommand(data.getOrderId()))
            .to(OrderServiceChannels.orderServiceChannel)
            .build();
  }

  private CommandWithDestination beginReviseOrder(ReviseOrderSagaData data) {
    return send(new BeginReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
            .to(OrderServiceChannels.orderServiceChannel)
            .build();

  }



}

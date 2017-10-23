package net.chrisrichardson.ftgo.orderservice.sagas.cancelorder;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.ftgo.accountservice.api.AccountingServiceChannels;
import net.chrisrichardson.ftgo.accountservice.api.ReverseAuthorizationCommand;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.BeginCancelCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ConfirmCancelOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.UndoBeginCancelCommand;
import net.chrisrichardson.ftgo.restaurantorderservice.api.BeginCancelRestaurantOrderCommand;
import net.chrisrichardson.ftgo.restaurantorderservice.api.ConfirmCancelRestaurantOrderCommand;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderServiceChannels;
import net.chrisrichardson.ftgo.restaurantorderservice.api.UndoBeginCancelRestaurantOrderCommand;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class CancelOrderSaga implements SimpleSaga<CancelOrderSagaData> {



  private SagaDefinition<CancelOrderSagaData> sagaDefinition;


  @PostConstruct
  public void initializeSagaDefinition() {
    sagaDefinition = step()
            .invokeParticipant(this::beginCancel)
            .withCompensation(this::undoBeginCancel)
            .step()
            .invokeParticipant(this::beginCancelRestaurantOrder)
            .withCompensation(this::undoBeginCancelRestaurantOrder)
            .step()
            .invokeParticipant(this::reverseAuthorization)
            .step()
            .invokeParticipant(this::confirmRestaurantOrderCancel)
            .step()
            .invokeParticipant(this::confirmOrderCancel)
            .build();

  }

  private CommandWithDestination confirmOrderCancel(CancelOrderSagaData data) {
    return send(new ConfirmCancelOrderCommand(data.getOrderId()))
            .to(OrderServiceChannels.orderServiceChannel)
            .build();

  }

  private CommandWithDestination confirmRestaurantOrderCancel(CancelOrderSagaData data) {
    return send(new ConfirmCancelRestaurantOrderCommand(data.getRestaurantId(), data.getOrderId()))
            .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
            .build();

  }

  private CommandWithDestination reverseAuthorization(CancelOrderSagaData data) {
    return send(new ReverseAuthorizationCommand(data.getConsumerId(), data.getOrderId(), data.getOrderTotal()))
            .to(AccountingServiceChannels.accountingServiceChannel)
            .build();

  }

  private CommandWithDestination undoBeginCancelRestaurantOrder(CancelOrderSagaData data) {
    return send(new UndoBeginCancelRestaurantOrderCommand(data.getRestaurantId(), data.getOrderId()))
            .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
            .build();

  }

  private CommandWithDestination beginCancelRestaurantOrder(CancelOrderSagaData data) {
    return send(new BeginCancelRestaurantOrderCommand(data.getRestaurantId(), (long) data.getOrderId()))
            .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
            .build();

  }

  private CommandWithDestination undoBeginCancel(CancelOrderSagaData data) {
    return send(new UndoBeginCancelCommand(data.getOrderId()))
            .to(OrderServiceChannels.orderServiceChannel)
            .build();
  }

  private CommandWithDestination beginCancel(CancelOrderSagaData data) {
    return send(new BeginCancelCommand(data.getOrderId()))
            .to(OrderServiceChannels.orderServiceChannel)
            .build();
  }


  @Override
  public SagaDefinition<CancelOrderSagaData> getSagaDefinition() {
    Assert.notNull(sagaDefinition);
    return sagaDefinition;
  }



}

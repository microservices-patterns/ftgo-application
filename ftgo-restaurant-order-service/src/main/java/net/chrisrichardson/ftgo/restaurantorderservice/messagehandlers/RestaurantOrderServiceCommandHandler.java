package net.chrisrichardson.ftgo.restaurantorderservice.messagehandlers;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.*;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantDetailsVerificationException;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantOrderService;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;
import static io.eventuate.tram.sagas.participant.SagaReplyMessageBuilder.withLock;

public class RestaurantOrderServiceCommandHandler {

  @Autowired
  private RestaurantOrderService restaurantOrderService;

  public CommandHandlers commandHandlers() {
    return SagaCommandHandlersBuilder
            .fromChannel("restaurantOrderService")
            .onMessage(CreateRestaurantOrder.class, this::createRestaurantOrder)
            .onMessage(ConfirmCreateRestaurantOrder.class, this::confirmCreateRestaurantOrder)
            .onMessage(CancelCreateRestaurantOrder.class, this::cancelCreateRestaurantOrder)

            .onMessage(BeginCancelRestaurantOrderCommand.class, this::beginCancelRestaurantOrder)
            .onMessage(ConfirmCancelRestaurantOrderCommand.class, this::confirmCancelRestaurantOrder)
            .onMessage(UndoBeginCancelRestaurantOrderCommand.class, this::undoBeginCancelRestaurantOrder)

            .onMessage(BeginReviseRestaurantOrderCommand.class, this::beginReviseRestaurantOrder)
            .onMessage(UndoBeginReviseRestaurantOrderCommand.class, this::undoBeginReviseRestaurantOrder)
            .onMessage(ConfirmReviseRestaurantOrderCommand.class, this::confirmReviseRestaurantOrder)
            .build();
  }

  private Message createRestaurantOrder(CommandMessage<CreateRestaurantOrder>
                                                cm) {
    CreateRestaurantOrder command = cm.getCommand();
    long restaurantId = command.getRestaurantId();
    Long restaurantOrderId = command.getOrderId();
    RestaurantOrderDetails restaurantOrderDetails = command.getRestaurantOrderDetails();


    try {
      RestaurantOrder restaurantOrder = restaurantOrderService.createRestaurantOrder(restaurantId, restaurantOrderId, restaurantOrderDetails);
      CreateRestaurantOrderReply reply = new CreateRestaurantOrderReply(restaurantOrder.getId());
      return withLock(RestaurantOrder.class, restaurantOrder.getId()).withSuccess(reply);
    } catch (RestaurantDetailsVerificationException e) {
      return withFailure();
    }
  }

  private Message confirmCreateRestaurantOrder
          (CommandMessage<ConfirmCreateRestaurantOrder> cm) {
    Long restaurantOrderId = cm.getCommand().getRestaurantOrderId();
    restaurantOrderService.confirmCreateRestaurantOrder(restaurantOrderId);
    return withSuccess();
  }

  private Message cancelCreateRestaurantOrder
          (CommandMessage<CancelCreateRestaurantOrder> cm) {
    Long restaurantOrderId = cm.getCommand().getRestaurantOrderId();
    restaurantOrderService.cancelCreateRestaurantOrder(restaurantOrderId);
    return withSuccess();
  }


  private Message beginCancelRestaurantOrder(CommandMessage<BeginCancelRestaurantOrderCommand> cm) {
    restaurantOrderService.cancelRestaurantOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return withSuccess();
  }
  private Message confirmCancelRestaurantOrder(CommandMessage<ConfirmCancelRestaurantOrderCommand> cm) {
    restaurantOrderService.confirmCancelRestaurantOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return withSuccess();
  }

  private Message undoBeginCancelRestaurantOrder(CommandMessage<UndoBeginCancelRestaurantOrderCommand> cm) {
    restaurantOrderService.undoCancel(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return withSuccess();
  }

  public Message beginReviseRestaurantOrder(CommandMessage<BeginReviseRestaurantOrderCommand> cm) {
    restaurantOrderService.beginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedLineItemQuantities());
    return withSuccess();
  }

  public Message undoBeginReviseRestaurantOrder(CommandMessage<UndoBeginReviseRestaurantOrderCommand> cm) {
    restaurantOrderService.undoBeginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return withSuccess();
  }

  public Message confirmReviseRestaurantOrder(CommandMessage<ConfirmReviseRestaurantOrderCommand> cm) {
    restaurantOrderService.confirmReviseRestaurantOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedLineItemQuantities());
    return withSuccess();
  }


}


package net.chrisrichardson.ftgo.orderservice.service;

import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRepository;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRevision;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.orderservice.domain.RevisedOrder;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.*;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import net.chrisrichardson.ftgo.common.UnsupportedStateTransitionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class OrderCommandHandlers {

  @Autowired
  private OrderService orderService;

  public CommandHandlers commandHandlers() {
    return SagaCommandHandlersBuilder
          .fromChannel("orderService")
          .onMessage(ApproveOrderCommand.class, this::approveOrder)
          .onMessage(RejectOrderCommand.class, this::rejectOrder)

          .onMessage(BeginCancelCommand.class, this::beginCancel)
          .onMessage(UndoBeginCancelCommand.class, this::undoCancel)
          .onMessage(ConfirmCancelOrderCommand.class, this::confirmCancel)

          .onMessage(BeginReviseOrderCommand.class, this::beginReviseOrder)
          .onMessage(UndoBeginReviseOrderCommand.class, this::undoPendingRevision)
          .onMessage(ConfirmReviseOrderCommand.class, this::confirmRevision)
          .build();

  }

  public Message approveOrder(CommandMessage<ApproveOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    orderService.approveOrder(orderId);
    return withSuccess();
  }


  public Message rejectOrder(CommandMessage<RejectOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    orderService.rejectOrder(orderId);
    return withSuccess();
  }


  public Message beginCancel(CommandMessage<BeginCancelCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    try {
      orderService.beginCancel(orderId);
      return withSuccess();
    } catch (UnsupportedStateTransitionException e) {
      return withFailure();
    }
  }


  public Message undoCancel(CommandMessage<UndoBeginCancelCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    orderService.undoCancel(orderId);
    return withSuccess();
  }

  public Message confirmCancel(CommandMessage<ConfirmCancelOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    orderService.confirmCancelled(orderId);
    return withSuccess();
  }


  public Message beginReviseOrder(CommandMessage<BeginReviseOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    OrderRevision revision = cm.getCommand().getRevision();
    try {
      return orderService.beginReviseOrder(orderId, revision).map(result -> withSuccess(new BeginReviseOrderReply(result.getChange().getNewOrderTotal()))).orElseGet(CommandHandlerReplyBuilder::withFailure);
    } catch (UnsupportedStateTransitionException e) {
      return withFailure();
    }
  }

  public Message undoPendingRevision(CommandMessage <UndoBeginReviseOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    orderService.undoPendingRevision(orderId);
    return withSuccess();
  }

  public Message confirmRevision(CommandMessage<ConfirmReviseOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    OrderRevision revision = cm.getCommand().getRevision();
    orderService.confirmRevision(orderId, revision);
    return withSuccess();
  }

}

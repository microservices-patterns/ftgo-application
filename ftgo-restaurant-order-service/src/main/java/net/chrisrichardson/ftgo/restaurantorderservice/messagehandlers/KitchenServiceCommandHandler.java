package net.chrisrichardson.ftgo.restaurantorderservice.messagehandlers;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.*;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantDetailsVerificationException;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.Ticket;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.KitchenService;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;
import static io.eventuate.tram.sagas.participant.SagaReplyMessageBuilder.withLock;

public class KitchenServiceCommandHandler {

  @Autowired
  private KitchenService kitchenService;

  public CommandHandlers commandHandlers() {
    return SagaCommandHandlersBuilder
            .fromChannel(KitchenServiceChannels.kitchenServiceChannel)
            .onMessage(CreateTicket.class, this::createRestaurantOrder)
            .onMessage(ConfirmCreateTicket.class, this::confirmCreateRestaurantOrder)
            .onMessage(CancelCreateTicket.class, this::cancelCreateRestaurantOrder)

            .onMessage(BeginCancelTicketCommand.class, this::beginCancelRestaurantOrder)
            .onMessage(ConfirmCancelTicketCommand.class, this::confirmCancelRestaurantOrder)
            .onMessage(UndoBeginCancelTicketCommand.class, this::undoBeginCancelRestaurantOrder)

            .onMessage(BeginReviseTicketCommand.class, this::beginReviseRestaurantOrder)
            .onMessage(UndoBeginReviseTicketCommand.class, this::undoBeginReviseRestaurantOrder)
            .onMessage(ConfirmReviseTicketCommand.class, this::confirmReviseRestaurantOrder)
            .build();
  }

  private Message createRestaurantOrder(CommandMessage<CreateTicket>
                                                cm) {
    CreateTicket command = cm.getCommand();
    long restaurantId = command.getRestaurantId();
    Long ticketId = command.getOrderId();
    TicketDetails ticketDetails = command.getTicketDetails();


    try {
      Ticket ticket = kitchenService.createRestaurantOrder(restaurantId, ticketId, ticketDetails);
      CreateTicketReply reply = new CreateTicketReply(ticket.getId());
      return withLock(Ticket.class, ticket.getId()).withSuccess(reply);
    } catch (RestaurantDetailsVerificationException e) {
      return withFailure();
    }
  }

  private Message confirmCreateRestaurantOrder
          (CommandMessage<ConfirmCreateTicket> cm) {
    Long ticketId = cm.getCommand().getTicketId();
    kitchenService.confirmCreateRestaurantOrder(ticketId);
    return withSuccess();
  }

  private Message cancelCreateRestaurantOrder
          (CommandMessage<CancelCreateTicket> cm) {
    Long ticketId = cm.getCommand().getTicketId();
    kitchenService.cancelCreateRestaurantOrder(ticketId);
    return withSuccess();
  }


  private Message beginCancelRestaurantOrder(CommandMessage<BeginCancelTicketCommand> cm) {
    kitchenService.cancelRestaurantOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return withSuccess();
  }
  private Message confirmCancelRestaurantOrder(CommandMessage<ConfirmCancelTicketCommand> cm) {
    kitchenService.confirmCancelRestaurantOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return withSuccess();
  }

  private Message undoBeginCancelRestaurantOrder(CommandMessage<UndoBeginCancelTicketCommand> cm) {
    kitchenService.undoCancel(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return withSuccess();
  }

  public Message beginReviseRestaurantOrder(CommandMessage<BeginReviseTicketCommand> cm) {
    kitchenService.beginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedLineItemQuantities());
    return withSuccess();
  }

  public Message undoBeginReviseRestaurantOrder(CommandMessage<UndoBeginReviseTicketCommand> cm) {
    kitchenService.undoBeginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return withSuccess();
  }

  public Message confirmReviseRestaurantOrder(CommandMessage<ConfirmReviseTicketCommand> cm) {
    kitchenService.confirmReviseRestaurantOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedLineItemQuantities());
    return withSuccess();
  }


}


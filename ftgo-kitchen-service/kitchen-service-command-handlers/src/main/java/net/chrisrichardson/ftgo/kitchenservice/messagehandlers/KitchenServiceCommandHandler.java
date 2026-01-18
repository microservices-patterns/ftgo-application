package net.chrisrichardson.ftgo.kitchenservice.messagehandlers;

import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.commands.consumer.annotations.EventuateCommandHandler;
import net.chrisrichardson.ftgo.kitchenservice.api.*;
import net.chrisrichardson.ftgo.kitchenservice.domain.KitchenService;
import net.chrisrichardson.ftgo.kitchenservice.domain.Ticket;

public class KitchenServiceCommandHandler {

  private final KitchenService kitchenService;

  public KitchenServiceCommandHandler(KitchenService kitchenService) {
    this.kitchenService = kitchenService;
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public CreateTicketReply createTicket(CommandMessage<CreateTicket> cm) {
    CreateTicket command = cm.getCommand();
    Ticket ticket = kitchenService.createTicket(command.getRestaurantId(), command.getOrderId(), command.getTicketDetails());
    return new CreateTicketReply(ticket.getId());
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public Success confirmCreateTicket(CommandMessage<ConfirmCreateTicket> cm) {
    kitchenService.confirmCreateTicket(cm.getCommand().getTicketId());
    return new Success();
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public Success cancelCreateTicket(CommandMessage<CancelCreateTicket> cm) {
    kitchenService.cancelCreateTicket(cm.getCommand().getTicketId());
    return new Success();
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public Success beginCancelTicket(CommandMessage<BeginCancelTicketCommand> cm) {
    kitchenService.cancelTicket(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return new Success();
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public Success confirmCancelTicket(CommandMessage<ConfirmCancelTicketCommand> cm) {
    kitchenService.confirmCancelTicket(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return new Success();
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public Success undoBeginCancelTicket(CommandMessage<UndoBeginCancelTicketCommand> cm) {
    kitchenService.undoCancel(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return new Success();
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public Success beginReviseTicket(CommandMessage<BeginReviseTicketCommand> cm) {
    kitchenService.beginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedOrderLineItems());
    return new Success();
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public Success undoBeginReviseTicket(CommandMessage<UndoBeginReviseTicketCommand> cm) {
    kitchenService.undoBeginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
    return new Success();
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public Success confirmReviseTicket(CommandMessage<ConfirmReviseTicketCommand> cm) {
    kitchenService.confirmReviseTicket(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedOrderLineItems());
    return new Success();
  }
}

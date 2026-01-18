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
  public void confirmCreateTicket(CommandMessage<ConfirmCreateTicket> cm) {
    kitchenService.confirmCreateTicket(cm.getCommand().getTicketId());
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public void cancelCreateTicket(CommandMessage<CancelCreateTicket> cm) {
    kitchenService.cancelCreateTicket(cm.getCommand().getTicketId());
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public void beginCancelTicket(CommandMessage<BeginCancelTicketCommand> cm) {
    kitchenService.cancelTicket(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public void confirmCancelTicket(CommandMessage<ConfirmCancelTicketCommand> cm) {
    kitchenService.confirmCancelTicket(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public void undoBeginCancelTicket(CommandMessage<UndoBeginCancelTicketCommand> cm) {
    kitchenService.undoCancel(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public void beginReviseTicket(CommandMessage<BeginReviseTicketCommand> cm) {
    kitchenService.beginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedOrderLineItems());
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public void undoBeginReviseTicket(CommandMessage<UndoBeginReviseTicketCommand> cm) {
    kitchenService.undoBeginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
  }

  @EventuateCommandHandler(subscriberId = "kitchenServiceCommands", channel = KitchenServiceChannels.COMMAND_CHANNEL)
  public void confirmReviseTicket(CommandMessage<ConfirmReviseTicketCommand> cm) {
    kitchenService.confirmReviseTicket(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedOrderLineItems());
  }
}

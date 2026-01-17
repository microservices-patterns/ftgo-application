package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantOperation;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantProxy;
import net.chrisrichardson.ftgo.kitchenservice.api.*;

@SagaParticipantProxy(channel = KitchenServiceChannels.COMMAND_CHANNEL)
public class KitchenServiceProxy {

  @SagaParticipantOperation(commandClass = CreateTicket.class, replyClasses = CreateTicketReply.class)
  public CommandWithDestination createTicket(long restaurantId, Long orderId, TicketDetails ticketDetails) {
    return CommandWithDestinationBuilder
            .send(new CreateTicket(restaurantId, orderId, ticketDetails))
            .to(KitchenServiceChannels.COMMAND_CHANNEL)
            .build();
  }

  @SagaParticipantOperation(commandClass = ConfirmCreateTicket.class, replyClasses = Success.class)
  public CommandWithDestination confirmCreateTicket(Long ticketId) {
    return CommandWithDestinationBuilder
            .send(new ConfirmCreateTicket(ticketId))
            .to(KitchenServiceChannels.COMMAND_CHANNEL)
            .build();
  }

  @SagaParticipantOperation(commandClass = CancelCreateTicket.class, replyClasses = Success.class)
  public CommandWithDestination cancelCreateTicket(long ticketId) {
    return CommandWithDestinationBuilder
            .send(new CancelCreateTicket(ticketId))
            .to(KitchenServiceChannels.COMMAND_CHANNEL)
            .build();
  }
}
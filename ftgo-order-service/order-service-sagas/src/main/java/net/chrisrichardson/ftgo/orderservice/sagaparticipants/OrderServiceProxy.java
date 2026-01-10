package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;

public class OrderServiceProxy {

  public final CommandEndpoint<RejectOrderCommand> reject = CommandEndpointBuilder
          .forCommand(RejectOrderCommand.class)
          .withChannel(OrderServiceChannels.COMMAND_CHANNEL)
          .withReply(Success.class)
          .build();

  public final CommandEndpoint<ApproveOrderCommand> approve = CommandEndpointBuilder
          .forCommand(ApproveOrderCommand.class)
          .withChannel(OrderServiceChannels.COMMAND_CHANNEL)
          .withReply(Success.class)
          .build();

}
package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;

import java.util.List;
import java.util.function.Function;

public class SagaParticipantStub<C>  {
  private Function<CommandMessage<C>, Message> replyBuilder;

  public SagaParticipantStub(Function<CommandMessage<C>, Message> replyBuilder) {
    this.replyBuilder = replyBuilder;
  }

  public List<Message> invoke(CommandMessage<Command> cm) {
    throw new UnsupportedOperationException();
  }
}

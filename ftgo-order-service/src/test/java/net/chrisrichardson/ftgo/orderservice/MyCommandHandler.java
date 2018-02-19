package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.tram.commands.consumer.CommandHandler;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;

public class MyCommandHandler<C> extends CommandHandler {

  private String commandChannel;
  private final Predicate<Message> expectedCommand;

  public  MyCommandHandler(String commandChannel, Class<C> expectedCommandClass, Predicate<Message> expectedCommand, Function<CommandMessage<C>, Message> replyBuilder) {
    super(commandChannel, Optional.empty(), expectedCommandClass, (cm, pv) -> singletonList(replyBuilder.apply(cm)));
    this.commandChannel = commandChannel;
    this.expectedCommand = expectedCommand;
  }

  @Override
  public boolean handles(Message message) {
    return message.getRequiredHeader(Message.DESTINATION).equals(commandChannel) && super.handles(message) && expectedCommand.test(message);
  }
}

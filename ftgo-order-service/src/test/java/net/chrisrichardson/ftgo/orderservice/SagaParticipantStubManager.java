package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.javaclient.commonimpl.JSonMapper;
import io.eventuate.tram.commands.common.ChannelMapping;
import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.consumer.CommandExceptionHandler;
import io.eventuate.tram.commands.consumer.CommandHandler;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.messaging.producer.MessageProducer;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class SagaParticipantStubManager {
  private final MyCommandHandlers commandHandlers;
  private Set<String> commandChannels;
  private final MyCommandDispatcher commandDispatcher;
  private String currentCommandChannel;





  class MyCommandHandlers extends CommandHandlers {

    private List<CommandHandler> myHandlers = new ArrayList<>();

    public MyCommandHandlers() {
      super(Collections.emptyList());
    }

    @Override
    public Set<String> getChannels() {
      return commandChannels;
    }

    public void add(CommandHandler commandHandler) {
      this.myHandlers.add(commandHandler);
    }

    @Override
    public Optional<CommandHandler> findTargetMethod(Message message) {
      return myHandlers.stream().filter(h -> h.handles(message)).findFirst();
    }

    @Override
    public Optional<CommandExceptionHandler> findExceptionHandler(Throwable cause) {
      return super.findExceptionHandler(cause);
    }

    public void reset() {
      myHandlers.clear();
    }
  }

  public SagaParticipantStubManager(MessagingStubConfiguration messagingStubConfiguration, ChannelMapping channelMapping, MessageConsumer messageConsumer, MessageProducer messageProducer) {
    this.commandChannels = messagingStubConfiguration.getChannels();
    this.commandHandlers = new MyCommandHandlers();
    this.commandDispatcher = new MyCommandDispatcher("SagaParticipantStubManager-command-dispatcher-" + System.currentTimeMillis(),
            commandHandlers,
            channelMapping,
            messageConsumer,
            messageProducer);

    /// TODO handle scenario where a command is recieved for which there is not a handler.
  }


  @PostConstruct
  public void initialize() {
    commandDispatcher.initialize();
  }

  public void reset() {
    commandHandlers.reset();
    commandDispatcher.reset();

  }

  public SagaParticipantStubManager forChannel(String commandChannel) {
    validateChannel(commandChannel);
    this.currentCommandChannel = commandChannel;
    return this;
  }

  private void validateChannel(String commandChannel) {
    if (!commandChannels.contains(commandChannel))
      throw new IllegalArgumentException(String.format("%s is not one of the specified channels: %s", commandChannel, commandChannels));
  }

  public <C extends Command> SagaParticipantStubManagerHelper<C> when(C expectedCommand) {
    return new SagaParticipantStubManagerHelper<C>(this, (Class<C>) expectedCommand.getClass(),
            message -> JSonMapper.fromJson(message.getPayload(), expectedCommand.getClass()).equals(expectedCommand));
  }

  public <C extends Command> SagaParticipantStubManagerHelper<C> when(Class<C> expectedCommandClass) {
    return new SagaParticipantStubManagerHelper<C>(this, expectedCommandClass, message -> true);
  }


  public class SagaParticipantStubManagerHelper<C>  {
    private Class<C> expectedCommandClass;
    private final Predicate<Message> expectedCommand;
    private SagaParticipantStubManager sagaParticipantStubManager;

    public SagaParticipantStubManagerHelper(SagaParticipantStubManager sagaParticipantStubManager, Class<C> expectedCommandClass, Predicate<Message> expectedCommand) {
      this.sagaParticipantStubManager = sagaParticipantStubManager;
      this.expectedCommandClass = expectedCommandClass;
      this.expectedCommand = expectedCommand;
    }

    public SagaParticipantStubManager replyWith(Function<CommandMessage<C>, Message> replyBuilder) {
      MyCommandHandler<C> commandHandler = new MyCommandHandler<>(currentCommandChannel, expectedCommandClass, expectedCommand, replyBuilder);
      sagaParticipantStubManager.commandHandlers.add(commandHandler);
      commandDispatcher.noteNewCommandHandler(commandHandler);
      return sagaParticipantStubManager;
    }

    public SagaParticipantStubManager replyWithSuccess() {
      return replyWith(cm -> withSuccess());
    }

    public SagaParticipantStubManager replyWithFailure() {
      return replyWith(cm -> withFailure());
    }
  }

}

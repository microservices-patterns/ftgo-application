package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.common.EventMessageHeaders;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.util.test.async.Eventually;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.fail;

public class MessageTracker {

  private Set<String> channels;

  private LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();

  public MessageTracker(MessageTrackerConfiguration config, MessageConsumer messageConsumer) {
    this.channels = config.getChannels();
    messageConsumer.subscribe("SagaParticipantStubManager-messages-" + System.currentTimeMillis(), channels, this::handleMessage);
  }

  private void handleMessage(Message message) {
    messages.add(message);
  }

  private void validateChannel(String commandChannel) {
    if (!channels.contains(commandChannel))
      throw new IllegalArgumentException(String.format("%s is not one of the specified channels: %s", commandChannel, channels));
  }

  public void reset() {
    messages.clear();
  }

  public <C extends Command> void assertCommandMessageSent(String channel, Class<C> expectedCommandClass) {
    validateChannel(channel);
    Eventually.eventually(() -> {
      List<Message> messages = Arrays.asList(this.messages.toArray(new Message[this.messages.size()]));
      if (messages.stream()
              .noneMatch(m -> m.getHeader(CommandMessageHeaders.COMMAND_TYPE).map(ct -> ct.equals(expectedCommandClass.getName())).orElse(false)))
        fail(String.format("Cannot find command message of type %s in %s", expectedCommandClass.getName(), messages));
    });

  }


  public <C extends DomainEvent> void assertDomainEventPublished(String channel, Class<C> expectedDomainEventClass) {
    validateChannel(channel);
    Eventually.eventually(() -> {
      List<Message> messages = Arrays.asList(this.messages.toArray(new Message[this.messages.size()]));
      if (messages.stream()
              .noneMatch(m -> m.getHeader(EventMessageHeaders.EVENT_TYPE).map(ct -> ct.equals(expectedDomainEventClass.getName())).orElse(false)))
        fail(String.format("Cannot find domain eventmessage of type %s in %s", expectedDomainEventClass.getName(), messages));
    });
  }
}

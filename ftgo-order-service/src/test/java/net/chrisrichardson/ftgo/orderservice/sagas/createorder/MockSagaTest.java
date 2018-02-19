package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.javaclient.commonimpl.JSonMapper;
import io.eventuate.javaclient.spring.jdbc.IdGeneratorImpl;
import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.commands.common.CommandReplyOutcome;
import io.eventuate.tram.commands.common.DefaultChannelMapping;
import io.eventuate.tram.commands.common.Failure;
import io.eventuate.tram.commands.common.ReplyMessageHeaders;
import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.commands.producer.CommandProducerImpl;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.producer.MessageBuilder;
import io.eventuate.tram.sagas.orchestration.*;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class MockSagaTest {

  private final IdGeneratorImpl idGenerator = new IdGeneratorImpl();
  private SagaManagerImpl sagaManager;
  private Command expectedCommand;

  private List<MessageWithDestination> sentCommands = new ArrayList<>();
  private MessageWithDestination sentCommand;

  static MockSagaTest given() {
    return new MockSagaTest();
  }

  public <T> MockSagaTest saga(SimpleSaga<T> saga, T sagaData) {
    sagaManager = new SagaManagerImpl<>(saga);
    sagaManager.setSagaInstanceRepository(new SagaInstanceRepository() {

      private SagaInstance sagaInstance;

      @Override
      public void save(SagaInstance sagaInstance) {
        sagaInstance.setId(idGenerator.genId().asString());
        this.sagaInstance = sagaInstance;
      }

      @Override
      public SagaInstance find(String sagaType, String sagaId) {
        return sagaInstance;
      }

      @Override
      public void update(SagaInstance sagaInstance) {
        this.sagaInstance = sagaInstance;
      }

      @Override
      public <Data> SagaInstanceData<Data> findWithData(String sagaType, String sagaId) {
        SagaInstance sagaInstance = find(sagaType, sagaId);
        Data sagaData = SagaDataSerde.deserializeSagaData(sagaInstance.getSerializedSagaData());
        return new SagaInstanceData<>(sagaInstance, sagaData);
      }
    });
    sagaManager.setIdGenerator(idGenerator);

    CommandProducerImpl commandProducer = new CommandProducerImpl((destination, message) -> {
      String id = idGenerator.genId().asString();
      message.getHeaders().put(Message.ID, id);
      sentCommands.add(new MessageWithDestination(destination, message));
    }, new DefaultChannelMapping(Collections.emptyMap()));

    sagaManager.setCommandProducer(commandProducer);
    sagaManager.setSagaCommandProducer(new SagaCommandProducer(commandProducer));


    sagaManager.setAggregateInstanceSubscriptionsDAO(mock(AggregateInstanceSubscriptionsDAO.class));
    
    sagaManager.create(sagaData);
    return this;
  }

  public MockSagaTest expect() {
    return this;
  }

  public MockSagaTest command(Command command) {
    expectedCommand = command;
    return this;
  }

  public MockSagaTest to(String commandChannel) {
    assertEquals(1, sentCommands.size());
    sentCommand = sentCommands.get(0);
    assertEquals(commandChannel, sentCommand.getDestination());
    assertEquals(expectedCommand.getClass().getName(), sentCommand.getMessage().getRequiredHeader(CommandMessageHeaders.COMMAND_TYPE));
    // TODO 
    sentCommands.clear();
    return this;
  }

  public MockSagaTest andGiven() {
    return this;
  }

  // copy
  private Map<String, String> correlationHeaders(Map<String, String> headers) {
    Map<String, String> m = headers.entrySet()
            .stream()
            .filter(e -> e.getKey().startsWith(CommandMessageHeaders.COMMAND_HEADER_PREFIX))
            .collect(Collectors.toMap(e -> CommandMessageHeaders.inReply(e.getKey()),
                    Map.Entry::getValue));
    m.put(ReplyMessageHeaders.IN_REPLY_TO, headers.get(Message.ID));
    return m;
  }


  public MockSagaTest successReply() {
    Success reply = new Success();
    CommandReplyOutcome outcome = CommandReplyOutcome.SUCCESS;
    Message message = replyMessage(reply, outcome);
    String id = idGenerator.genId().asString();
    message.getHeaders().put(Message.ID, id);
    sagaManager.handleMessage(message);
    return this;
  }

  public MockSagaTest failureReply() {
    Failure reply = new Failure();
    CommandReplyOutcome outcome = CommandReplyOutcome.FAILURE;
    Message message = replyMessage(reply, outcome);
    String id = idGenerator.genId().asString();
    message.getHeaders().put(Message.ID, id);
    sagaManager.handleMessage(message);
    return this;
  }

  private Message replyMessage(Object reply, CommandReplyOutcome outcome) {
    return MessageBuilder
            .withPayload(JSonMapper.toJson(reply))
            .withHeader(ReplyMessageHeaders.REPLY_OUTCOME, outcome.name())
            .withHeader(ReplyMessageHeaders.REPLY_TYPE, reply.getClass().getName())
            .withExtraHeaders("", correlationHeaders(sentCommand.getMessage().getHeaders()))
            .build();
  }
}

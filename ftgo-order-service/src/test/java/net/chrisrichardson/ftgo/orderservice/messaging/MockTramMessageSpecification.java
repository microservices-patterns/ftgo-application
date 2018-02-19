package net.chrisrichardson.ftgo.orderservice.messaging;

import io.eventuate.javaclient.spring.jdbc.IdGenerator;
import io.eventuate.javaclient.spring.jdbc.IdGeneratorImpl;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisherImpl;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.consumer.MessageHandler;

import java.util.Collections;

public class MockTramMessageSpecification {
  private MessageHandler handler;
  private String aggregateType;
  private Object aggregateId;
  private DomainEventDispatcher dispatcher;
  private IdGenerator idGenerator = new IdGeneratorImpl();
  
  public MockTramMessageSpecification eventHandlers(DomainEventHandlers domainEventHandlers) {
    this.dispatcher = new DomainEventDispatcher("MockId", domainEventHandlers, (subscriberId, channels, handler) -> MockTramMessageSpecification.this.handler = handler);
    dispatcher.initialize();
    return this;
  }

  public MockTramMessageSpecification when() {
    return this;
  }

  public MockTramMessageSpecification then() {
    return this;
  }

  public MockTramMessageSpecification aggregate(String aggregateType, Object aggregateId) {
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    return this;
  }


  public MockTramMessageSpecification publishes(DomainEvent event) {
    DomainEventPublisher publisher = new DomainEventPublisherImpl((destination, message) -> {
      String id = idGenerator.genId().asString();
      message.getHeaders().put(Message.ID, id);
      handler.accept(message);
    });

    publisher.publish(aggregateType, aggregateId, Collections.singletonList(event));
    return this;
  }

  public MockTramMessageSpecification verify(Runnable r) {
    r.run();
    return this;
  }
}

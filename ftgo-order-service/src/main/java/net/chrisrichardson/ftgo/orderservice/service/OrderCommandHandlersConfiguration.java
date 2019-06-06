package net.chrisrichardson.ftgo.orderservice.service;

import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.sagas.common.SagaLockManager;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaParticipantConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({SagaParticipantConfiguration.class, TramEventsPublisherConfiguration.class, CommonConfiguration.class})
public class OrderCommandHandlersConfiguration {

  @Bean
  public OrderCommandHandlers orderCommandHandlers() {
    return new OrderCommandHandlers();
  }

  @Bean
  public SagaCommandDispatcher orderCommandHandlersDispatcher(OrderCommandHandlers orderCommandHandlers,
                                                              MessageConsumer messageConsumer,
                                                              MessageProducer messageProducer,
                                                              SagaLockManager sagaLockManager) {
    return new SagaCommandDispatcher("orderService",
            orderCommandHandlers.commandHandlers(),
            messageConsumer,
            messageProducer,
            sagaLockManager);
  }

}

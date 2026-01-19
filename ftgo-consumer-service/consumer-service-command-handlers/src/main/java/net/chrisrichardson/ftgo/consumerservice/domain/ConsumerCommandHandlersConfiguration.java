package net.chrisrichardson.ftgo.consumerservice.domain;

import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ConsumerDomainConfiguration.class)
public class ConsumerCommandHandlersConfiguration {

  @Bean
  public ConsumerServiceCommandHandlers consumerServiceCommandHandlers() {
    return new ConsumerServiceCommandHandlers();
  }

  @Bean
  public CommandDispatcher commandDispatcher(ConsumerServiceCommandHandlers consumerServiceCommandHandlers,
                                             SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
    return sagaCommandDispatcherFactory.make("consumerServiceDispatcher", consumerServiceCommandHandlers.commandHandlers());
  }
}

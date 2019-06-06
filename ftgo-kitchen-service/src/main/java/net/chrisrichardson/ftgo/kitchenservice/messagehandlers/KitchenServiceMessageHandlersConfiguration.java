package net.chrisrichardson.ftgo.kitchenservice.messagehandlers;

import io.eventuate.tram.events.common.DefaultDomainEventNameMapping;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.sagas.common.SagaLockManager;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaParticipantConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.kitchenservice.domain.KitchenDomainConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KitchenDomainConfiguration.class, SagaParticipantConfiguration.class, CommonConfiguration.class})
public class KitchenServiceMessageHandlersConfiguration {

  @Bean
  public KitchenServiceEventConsumer ticketEventConsumer() {
    return new KitchenServiceEventConsumer();
  }

  @Bean
  public KitchenServiceCommandHandler kitchenServiceCommandHandler() {
    return new KitchenServiceCommandHandler();
  }

  @Bean
  public SagaCommandDispatcher kitchenServiceSagaCommandDispatcher(KitchenServiceCommandHandler kitchenServiceCommandHandler,
                                                                   MessageConsumer messageConsumer,
                                                                   MessageProducer messageProducer,
                                                                   SagaLockManager sagaLockManager) {
    return new SagaCommandDispatcher("kitchenServiceCommands",
            kitchenServiceCommandHandler.commandHandlers(),
            messageConsumer,
            messageProducer,
            sagaLockManager);
  }

  @Bean
  public DomainEventDispatcher domainEventDispatcher(KitchenServiceEventConsumer kitchenServiceEventConsumer, MessageConsumer messageConsumer) {
    return new DomainEventDispatcher("kitchenServiceEvents",
            kitchenServiceEventConsumer.domainEventHandlers(),
            messageConsumer,
            new DefaultDomainEventNameMapping()); // @Autowire
  }
}

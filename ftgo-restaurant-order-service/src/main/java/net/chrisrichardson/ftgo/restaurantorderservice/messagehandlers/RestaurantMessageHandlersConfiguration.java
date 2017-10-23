package net.chrisrichardson.ftgo.restaurantorderservice.messagehandlers;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaParticipantConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantOrderDomainConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RestaurantOrderDomainConfiguration.class, SagaParticipantConfiguration.class, CommonConfiguration.class})
public class RestaurantMessageHandlersConfiguration {

  @Bean
  public RestaurantOrderEventConsumer restaurantOrderEventConsumer() {
    return new RestaurantOrderEventConsumer();
  }

  @Bean
  public RestaurantOrderServiceCommandHandler restaurantOrderServiceCommandHandler() {
    return new RestaurantOrderServiceCommandHandler();
  }

  @Bean
  public SagaCommandDispatcher restaurantOrderServiceSagaCommandDispatcher(RestaurantOrderServiceCommandHandler restaurantOrderServiceCommandHandler) {
    return new SagaCommandDispatcher("restaurantOrderServiceCommands", restaurantOrderServiceCommandHandler.commandHandlers());
  }

  @Bean
  public DomainEventDispatcher domainEventDispatcher(RestaurantOrderEventConsumer restaurantOrderEventConsumer, MessageConsumer messageConsumer) {
    return new DomainEventDispatcher("restaurantOrderServiceEvents", restaurantOrderEventConsumer.domainEventHandlers(), messageConsumer); // @Autowire
  }
}

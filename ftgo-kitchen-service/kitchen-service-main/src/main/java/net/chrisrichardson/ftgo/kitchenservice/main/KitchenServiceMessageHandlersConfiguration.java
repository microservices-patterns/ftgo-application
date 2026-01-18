package net.chrisrichardson.ftgo.kitchenservice.main;

import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.spring.flyway.EventuateTramFlywayMigrationConfiguration;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.kitchenservice.domain.KitchenDomainConfiguration;
import net.chrisrichardson.ftgo.kitchenservice.domain.KitchenService;
import net.chrisrichardson.ftgo.kitchenservice.messagehandlers.KitchenServiceEventConsumer;
import net.chrisrichardson.ftgo.kitchenservice.messagehandlers.KitchenServiceCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KitchenDomainConfiguration.class, CommonConfiguration.class, TramEventSubscriberConfiguration.class, EventuateTramFlywayMigrationConfiguration.class})
public class KitchenServiceMessageHandlersConfiguration {

  @Bean
  public KitchenServiceEventConsumer ticketEventConsumer() {
    return new KitchenServiceEventConsumer();
  }

  @Bean
  public KitchenServiceCommandHandler kitchenServiceCommandHandler(KitchenService kitchenService) {
    return new KitchenServiceCommandHandler(kitchenService);
  }

  @Bean
  public DomainEventDispatcher domainEventDispatcher(KitchenServiceEventConsumer kitchenServiceEventConsumer, DomainEventDispatcherFactory domainEventDispatcherFactory) {
    return domainEventDispatcherFactory.make("kitchenServiceEvents", kitchenServiceEventConsumer.domainEventHandlers());
  }
}

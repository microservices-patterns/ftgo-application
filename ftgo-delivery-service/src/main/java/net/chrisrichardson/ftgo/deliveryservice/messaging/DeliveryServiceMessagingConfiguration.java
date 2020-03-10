package net.chrisrichardson.ftgo.deliveryservice.messaging;

import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.deliveryservice.domain.DeliveryService;
import net.chrisrichardson.ftgo.deliveryservice.domain.DeliveryServiceDomainConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DeliveryServiceDomainConfiguration.class, TramEventSubscriberConfiguration.class, CommonConfiguration.class})
public class DeliveryServiceMessagingConfiguration {

  @Bean
  public DeliveryMessageHandlers deliveryMessageHandlers(DeliveryService deliveryService) {
    return new DeliveryMessageHandlers(deliveryService);
  }

  @Bean
  public DomainEventDispatcher domainEventDispatcher(DeliveryMessageHandlers deliveryMessageHandlers, DomainEventDispatcherFactory domainEventDispatcherFactory) {
    return domainEventDispatcherFactory.make("deliveryService-deliveryMessageHandlers", deliveryMessageHandlers.domainEventHandlers());
  }
}

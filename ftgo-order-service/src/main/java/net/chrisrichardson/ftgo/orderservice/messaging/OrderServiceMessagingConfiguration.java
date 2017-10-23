package net.chrisrichardson.ftgo.orderservice.messaging;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import net.chrisrichardson.ftgo.orderservice.domain.OrderServiceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({OrderServiceConfiguration.class})
public class OrderServiceMessagingConfiguration {

  @Bean
  public OrderEventConsumer orderEventConsumer() {
    return new OrderEventConsumer();
  }

  @Bean
  public DomainEventDispatcher domainEventDispatcher(OrderEventConsumer orderEventConsumer, MessageConsumer messageConsumer) {
    return new DomainEventDispatcher("orderServiceEvents", orderEventConsumer.domainEventHandlers(), messageConsumer); // @Autowire
  }

}

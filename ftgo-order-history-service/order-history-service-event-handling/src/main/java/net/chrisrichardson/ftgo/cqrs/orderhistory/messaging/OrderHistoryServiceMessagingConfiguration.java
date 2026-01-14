package net.chrisrichardson.ftgo.cqrs.orderhistory.messaging;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import net.chrisrichardson.ftgo.cqrs.orderhistory.OrderHistoryDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderHistoryServiceMessagingConfiguration {

  @Bean
  public OrderHistoryEventHandlers orderHistoryEventHandlers(OrderHistoryDao orderHistoryDao) {
    return new OrderHistoryEventHandlers(orderHistoryDao);
  }

  @Bean
  public DomainEventDispatcher domainEventDispatcher(OrderHistoryEventHandlers orderHistoryEventHandlers,
                                                     DomainEventDispatcherFactory domainEventDispatcherFactory) {
    return domainEventDispatcherFactory.make("orderHistoryServiceEvents",
            orderHistoryEventHandlers.domainEventHandlers());
  }
}

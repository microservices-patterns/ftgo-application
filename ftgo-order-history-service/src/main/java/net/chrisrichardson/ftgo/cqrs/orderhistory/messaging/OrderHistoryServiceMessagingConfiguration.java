package net.chrisrichardson.ftgo.cqrs.orderhistory.messaging;

import io.eventuate.messaging.kafka.consumer.MessageConsumerKafkaImpl;
import io.eventuate.tram.consumer.common.MessageConsumerImplementation;
import io.eventuate.tram.consumer.common.TramNoopDuplicateMessageDetectorConfiguration;
import io.eventuate.tram.consumer.wrappers.EventuateKafkaMessageConsumerWrapper;
import io.eventuate.tram.events.common.DefaultDomainEventNameMapping;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.cqrs.orderhistory.OrderHistoryDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CommonConfiguration.class, TramNoopDuplicateMessageDetectorConfiguration.class})
public class OrderHistoryServiceMessagingConfiguration {

  @Bean
  public MessageConsumerImplementation messageConsumerImplementation(MessageConsumerKafkaImpl messageConsumerKafka) {
    return new EventuateKafkaMessageConsumerWrapper(messageConsumerKafka);
  }

  @Bean
  public OrderHistoryEventHandlers orderHistoryEventHandlers(OrderHistoryDao orderHistoryDao) {
    return new OrderHistoryEventHandlers(orderHistoryDao);
  }

  @Bean
  public DomainEventDispatcher orderHistoryDomainEventDispatcher(OrderHistoryEventHandlers orderHistoryEventHandlers, MessageConsumer messageConsumer) {
    return new DomainEventDispatcher("orderHistoryDomainEventDispatcher",
            orderHistoryEventHandlers.domainEventHandlers(),
            messageConsumer,
            new DefaultDomainEventNameMapping());
  }

}

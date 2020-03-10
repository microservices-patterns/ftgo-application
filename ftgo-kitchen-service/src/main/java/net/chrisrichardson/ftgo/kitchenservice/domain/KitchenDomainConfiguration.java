package net.chrisrichardson.ftgo.kitchenservice.domain;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
@ComponentScan
@EntityScan
@Import({TramEventsPublisherConfiguration.class, CommonConfiguration.class})
public class KitchenDomainConfiguration {

  @Bean
  public KitchenService kitchenService() {
    return new KitchenService();
  }

  @Bean
  public TicketDomainEventPublisher restaurantAggregateEventPublisher(DomainEventPublisher domainEventPublisher) {
    return new TicketDomainEventPublisher(domainEventPublisher);
  }
}

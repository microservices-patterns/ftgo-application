package net.chrisrichardson.ftgo.restaurantservice.contract;

import io.eventuate.common.spring.jdbc.EventuateTransactionTemplateConfiguration;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.cloudcontractsupport.EventuateContractVerifierConfiguration;
import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantDomainEventPublisher;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantMenu;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DeliveryserviceMessagingBase.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
public abstract class DeliveryserviceMessagingBase {

  @Configuration
  @EnableAutoConfiguration
  @Import({EventuateContractVerifierConfiguration.class, TramEventsPublisherConfiguration.class, TramInMemoryConfiguration.class, EventuateTransactionTemplateConfiguration.class})
  public static class TestConfiguration {

    @Bean
    public RestaurantDomainEventPublisher orderAggregateEventPublisher(DomainEventPublisher eventPublisher) {
      return new RestaurantDomainEventPublisher(eventPublisher);
    }
  }


  @Autowired
  private RestaurantDomainEventPublisher restaurantDomainEventPublisher;

  protected void restaurantCreated() {
    Restaurant restaurant = new Restaurant("Yummy Indian", new RestaurantMenu(Collections.emptyList()));
    restaurant.setId(99L);
    restaurantDomainEventPublisher.publish(restaurant,
            Collections.singletonList(new RestaurantCreated(restaurant.getName(), new Address("1 Main Street", "Unit 99", "Oakland", "CA", "94611"),
                    restaurant.getMenu())));
  }

}

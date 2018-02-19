package net.chrisrichardson.ftgo.orderservice.cucumber;

import io.eventuate.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.orderservice.MessageTracker;
import net.chrisrichardson.ftgo.orderservice.MessageTrackerConfiguration;
import net.chrisrichardson.ftgo.orderservice.MessagingStubConfiguration;
import net.chrisrichardson.ftgo.orderservice.SagaParticipantStubConfiguration;
import net.chrisrichardson.ftgo.orderservice.SagaParticipantStubManager;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = OrderServiceComponentTestSpringContextConfiguration.TestConfiguration.class)
public abstract class OrderServiceComponentTestSpringContextConfiguration {


  static {
    CommonJsonMapperInitializer.registerMoneyModule();
  }

  private int port = 8082;
  private String host = System.getenv("DOCKER_HOST_IP");

  protected String baseUrl(String path) {
    return String.format("http://%s:%s%s", host, port, path);
  }

  @Configuration
  @EnableAutoConfiguration
  @Import({TramJdbcKafkaConfiguration.class, SagaParticipantStubConfiguration.class})
  @EnableJpaRepositories(basePackageClasses = RestaurantRepository.class) // Need to verify that the restaurant has been created. Replace with verifyRestaurantCreatedInOrderService
  @EntityScan(basePackageClasses = Order.class)
  public static class TestConfiguration {

    @Bean
    public MessagingStubConfiguration messagingStubConfiguration() {
      return new MessagingStubConfiguration("consumerService", "restaurantOrderService", "accountingService", "orderService");
    }

    @Bean
    public MessageTrackerConfiguration messageTrackerConfiguration() {
      return new MessageTrackerConfiguration("net.chrisrichardson.ftgo.orderservice.domain.Order");
    }
  }

  @Autowired
  protected SagaParticipantStubManager sagaParticipantStubManager;

  @Autowired
  protected MessageTracker messageTracker;

  @Autowired
  protected DomainEventPublisher domainEventPublisher;

  @Autowired
  protected RestaurantRepository restaurantRepository;



}

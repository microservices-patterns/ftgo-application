package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.tram.spring.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.util.test.async.Eventually;
import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;
import net.chrisrichardson.ftgo.kitchenservice.api.ConfirmCreateTicket;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicket;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import net.chrisrichardson.ftgo.orderservice.api.web.CreateOrderRequest;
import net.chrisrichardson.ftgo.orderservice.domain.RestaurantRepository;
import net.chrisrichardson.ftgo.orderservice.messaging.OrderServiceMessagingConfiguration;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ApproveOrderCommand;
import net.chrisrichardson.ftgo.orderservice.service.OrderCommandHandlersConfiguration;
import net.chrisrichardson.ftgo.orderservice.web.OrderWebConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractOrderServiceComponentTest {

  protected abstract String baseUrl(String path);

  @Configuration
  @Import({CommonMessagingStubConfiguration.class, OrderWebConfiguration.class, OrderServiceMessagingConfiguration.class, OrderCommandHandlersConfiguration.class,
          TramCommandProducerConfiguration.class})
  public static class CommonTestConfiguration {
  }

  @Configuration
  @Import(SagaParticipantStubConfiguration.class)
  public static class CommonMessagingStubConfiguration {

    @Bean
    public MessagingStubConfiguration messagingStubConfiguration() {
      return new MessagingStubConfiguration("consumerService", "kitchenService", "accountingService", "orderService");
    }

    @Bean
    public MessageTracker messageTracker(MessageConsumer messageConsumer) {
      return new MessageTracker(new MessageTrackerConfiguration("orderService"), messageConsumer) ;
    }

  }

  @Autowired
  private SagaParticipantStubManager sagaParticipantStubManager;

  @Autowired
  private DomainEventPublisher domainEventPublisher;

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private MessageTracker messageTracker;

  @Before
  public void setUp() throws Exception {
    sagaParticipantStubManager.reset();
  }

  @Test
  public void shouldCreateOrder() {

    // setup

    sagaParticipantStubManager.
            forChannel("consumerService")
            .when(ValidateOrderByConsumer.class).replyWith(cm -> withSuccess())
            .forChannel("kitchenService")
            .when(CreateTicket.class).replyWith(cm -> withSuccess(new CreateTicketReply(cm.getCommand().getOrderId())))
            .when(ConfirmCreateTicket.class).replyWithSuccess()
            .forChannel("accountingService")
            .when(AuthorizeCommand.class).replyWithSuccess()
            .forChannel("orderService")
            .when(ApproveOrderCommand.class).replyWithSuccess()
    ;


    domainEventPublisher.publish("net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant", RestaurantMother.AJANTA_ID,
            Collections.singletonList(RestaurantMother.makeAjantaRestaurantCreatedEvent()));


    Eventually.eventually(() -> {
      FtgoTestUtil.assertPresent(restaurantRepository.findById(RestaurantMother.AJANTA_ID));
    });

    // make HTTP request

    Integer orderId =
            given().
                    body(new CreateOrderRequest(OrderDetailsMother.CONSUMER_ID,
                            RestaurantMother.AJANTA_ID, Collections.singletonList(new CreateOrderRequest.LineItem(RestaurantMother.CHICKEN_VINDALOO_MENU_ITEM_ID,
                            OrderDetailsMother.CHICKEN_VINDALOO_QUANTITY)))).
                    contentType("application/json").
                    when().
                    post(baseUrl("/orders")).
                    then().
                    statusCode(200).
                    extract().
                    path("orderId");

    assertNotNull(orderId);

    // verify response
    // verify Order state
    // verify events published????

    messageTracker.assertCommandMessageSent("orderService", ApproveOrderCommand.class);

  }
}

package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.sagas.spring.inmemory.TramSagaInMemoryConfiguration;
// TODO: These classes are no longer available in current Eventuate versions
// import io.eventuate.tram.sagas.spring.testing.contract.EventuateTramSagasSpringCloudContractSupportConfiguration;
// import io.eventuate.tram.sagas.spring.testing.contract.SagaMessagingTestHelper;
// import io.eventuate.tram.spring.cloudcontractsupport.EventuateTramRoutesConfigurer;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicket;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import net.chrisrichardson.ftgo.kitchenservice.api.TicketDetails;
import net.chrisrichardson.ftgo.kitchenservice.api.TicketLineItem;
import net.chrisrichardson.ftgo.orderservice.OrderDetailsMother;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.BatchStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.CHICKEN_VINDALOO_QUANTITY;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO: This test needs to be updated to use current Eventuate contract testing APIs
// The following classes are no longer available:
// - SagaMessagingTestHelper
// - EventuateTramSagasSpringCloudContractSupportConfiguration
// - EventuateTramRoutesConfigurer
@SpringBootTest(classes= KitchenServiceProxyIntegrationTest.TestConfiguration.class,
        webEnvironment= SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids =
        {"net.chrisrichardson.ftgo:ftgo-kitchen-service"}
        )
@DirtiesContext
@Disabled("Needs update to current Eventuate contract testing APIs")
public class KitchenServiceProxyIntegrationTest {


  @Configuration
  @EnableAutoConfiguration
  @Import({TramSagaInMemoryConfiguration.class})
  // TODO: Add EventuateTramSagasSpringCloudContractSupportConfiguration.class when available
  public static class TestConfiguration {

    // TODO: Restore when EventuateTramRoutesConfigurer is available
    // @Bean
    // public EventuateTramRoutesConfigurer eventuateTramRoutesConfigurer(BatchStubRunner batchStubRunner) {
    //   return new EventuateTramRoutesConfigurer(batchStubRunner);
    // }

    @Bean
    public KitchenServiceProxy kitchenServiceProxy() {
      return new KitchenServiceProxy();
    }
  }

  // TODO: Restore when SagaMessagingTestHelper is available
  // @Autowired
  // private SagaMessagingTestHelper sagaMessagingTestHelper;

  @Autowired
  private KitchenServiceProxy kitchenServiceProxy;

  @Test
  public void shouldSuccessfullyCreateTicket() {
    // TODO: Restore test implementation when Eventuate contract testing APIs are available
    // CreateTicket command = new CreateTicket(AJANTA_ID, OrderDetailsMother.ORDER_ID,
    //         new TicketDetails(Collections.singletonList(new TicketLineItem(CHICKEN_VINDALOO_MENU_ITEM_ID, CHICKEN_VINDALOO, CHICKEN_VINDALOO_QUANTITY))));
    // CreateTicketReply expectedReply = new CreateTicketReply(OrderDetailsMother.ORDER_ID);
    // String sagaType = CreateOrderSaga.class.getName();
    //
    // CreateTicketReply reply = sagaMessagingTestHelper.sendAndReceiveCommand(kitchenServiceProxy.create, command, CreateTicketReply.class, sagaType);
    //
    // assertEquals(expectedReply, reply);
  }

}

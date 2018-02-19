package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.ChannelMapping;
import io.eventuate.tram.commands.common.DefaultChannelMapping;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import io.eventuate.tram.springcloudcontractsupport.EventuateContractVerifierConfiguration;
import net.chrisrichardson.ftgo.orderservice.EventuateTramRoutesConfigurer;
import net.chrisrichardson.ftgo.orderservice.OrderDetailsMother;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrderReply;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderLineItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.BatchStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Collections;

import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.CHICKEN_VINDALOO_QUANTITY;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.CHICKEN_VINDALOO;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.CHICKEN_VINDALOO_MENU_ITEM_ID;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= RestaurantOrderServiceProxyIntegrationTest.TestConfiguration.class,
        webEnvironment= SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids =
        {"net.chrisrichardson.ftgo.contracts:ftgo-restaurant-order-service-contracts"},
        workOffline = false)
@DirtiesContext
public class RestaurantOrderServiceProxyIntegrationTest {


  @Configuration
  @EnableAutoConfiguration
  @Import({TramCommandProducerConfiguration.class,
          TramInMemoryConfiguration.class, EventuateContractVerifierConfiguration.class})
  public static class TestConfiguration {

    @Bean
    public ChannelMapping channelMapping() {
      return new DefaultChannelMapping.DefaultChannelMappingBuilder().build();
    }


    /// TramSagaInMemoryConfiguration

    @Bean
    public DataSource dataSource() {
      EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
      return builder.setType(EmbeddedDatabaseType.H2)
              .addScript("eventuate-tram-embedded-schema.sql")
              .addScript("eventuate-tram-sagas-embedded.sql")
              .build();
    }


    @Bean
    public EventuateTramRoutesConfigurer eventuateTramRoutesConfigurer(BatchStubRunner batchStubRunner) {
      return new EventuateTramRoutesConfigurer(batchStubRunner);
    }

    @Bean
    public SagaMessagingTestHelper sagaMessagingTestHelper() {
      return new SagaMessagingTestHelper();
    }

    @Bean
    public SagaCommandProducer sagaCommandProducer() {
      return new SagaCommandProducer();
    }

    @Bean
    public RestaurantOrderServiceProxy restaurantOrderServiceProxy() {
      return new RestaurantOrderServiceProxy();
    }
  }

  @Autowired
  private SagaMessagingTestHelper sagaMessagingTestHelper;

  @Autowired
  private  RestaurantOrderServiceProxy restaurantOrderServiceProxy;

  @Test
  public void shouldSuccessfullyCreateRestaurantOrder() {
    CreateRestaurantOrder command = new CreateRestaurantOrder(AJANTA_ID, OrderDetailsMother.ORDER_ID,
            new RestaurantOrderDetails(Collections.singletonList(new RestaurantOrderLineItem(CHICKEN_VINDALOO_MENU_ITEM_ID, CHICKEN_VINDALOO, CHICKEN_VINDALOO_QUANTITY))));
    CreateRestaurantOrderReply expectedReply = new CreateRestaurantOrderReply(OrderDetailsMother.ORDER_ID);
    String sagaType = CreateOrderSaga.class.getName();

    CreateRestaurantOrderReply reply = sagaMessagingTestHelper.sendAndReceiveCommand(restaurantOrderServiceProxy.create, command, CreateRestaurantOrderReply.class, sagaType);

    assertEquals(expectedReply, reply);

  }

}
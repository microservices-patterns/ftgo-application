package net.chrisrichardson.ftgo.restaurantorderservice.contract;

import io.eventuate.tram.springcloudcontractsupport.EventuateContractVerifierConfiguration;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantOrderService;
import net.chrisrichardson.ftgo.restaurantorderservice.messagehandlers.RestaurantMessageHandlersConfiguration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AbstractRestaurantOrderConsumerContractTest.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
public abstract class AbstractRestaurantOrderConsumerContractTest {

  @Configuration
  @Import({RestaurantMessageHandlersConfiguration.class, EventuateContractVerifierConfiguration.class})
  public static class TestConfiguration {

    @Bean
    public RestaurantOrderService restaurantOrderService() {
      return mock(RestaurantOrderService.class);
    }

  }

  @Autowired
  private RestaurantOrderService restaurantOrderService;

  @Before
  public void setup() {
     reset(restaurantOrderService);
     when(restaurantOrderService.createRestaurantOrder(eq(1L), eq(99L), any(RestaurantOrderDetails.class)))
             .thenReturn(new RestaurantOrder(1L, 99L, new RestaurantOrderDetails(Collections.emptyList())));
  }

}

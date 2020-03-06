package net.chrisrichardson.ftgo.deliveryservice.domain;

import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DeliveryJpaTest.Config.class)
public class DeliveryJpaTest {

  @Configuration
  @EnableJpaRepositories
  @EnableAutoConfiguration(exclude = TramConsumerJdbcAutoConfiguration.class)
  public static class Config {
  }

  @Autowired
  private DeliveryRepository deliveryRepository;

  @Test
  public void shouldSaveAndLoadDelivery() {
    long restaurantId = 102L;
    long orderId = System.currentTimeMillis();
    Delivery delivery = Delivery.create(orderId,
            restaurantId, DeliveryServiceTestData.PICKUP_ADDRESS, DeliveryServiceTestData.PICKUP_ADDRESS );
    Delivery savedDelivery = deliveryRepository.save(delivery);

    Delivery loadedDelivery = deliveryRepository.findById(orderId).get();
    assertNull(loadedDelivery.getAssignedCourier());
  }

}

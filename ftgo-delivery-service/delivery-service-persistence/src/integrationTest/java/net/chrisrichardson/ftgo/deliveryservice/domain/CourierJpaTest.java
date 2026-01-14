package net.chrisrichardson.ftgo.deliveryservice.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CourierJpaTest.Config.class)
@Testcontainers
class CourierJpaTest {

  @Configuration
  @EnableJpaRepositories
  @EnableAutoConfiguration
  public static class Config {
  }

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("eventuate")
          .withUsername("postgresuser")
          .withPassword("postgrespw");

  @DynamicPropertySource
  static void postgresProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }

  @Autowired
  private CourierRepository courierRepository;


  @Autowired
  private TransactionTemplate transactionTemplate;

  @Test
  public void shouldSaveAndLoad() {
    long courierId = System.currentTimeMillis();
    Courier courier = Courier.create(courierId);
    long deliveryId = 103L;
    courier.addAction(Action.makePickup(deliveryId, DeliveryServiceTestData.PICKUP_ADDRESS, LocalDateTime.now()));

    Courier savedCourier = courierRepository.save(courier);

    transactionTemplate.execute((ts) -> {
      Courier loadedCourier = courierRepository.findById(courierId).get();
      assertEquals(1, loadedCourier.getPlan().getActions().size());
      return null;
    });
  }

  @Test
  public void shouldFindAllAvailable() {
    long courierId1 = System.currentTimeMillis() * 10;
    long courierId2 = System.currentTimeMillis() * 10 + 1;
    Courier courier1 = Courier.create(courierId1);
    Courier courier2 = Courier.create(courierId2);

    courier1.noteAvailable();
    courier2.noteUnavailable();

    courierRepository.save(courier1);
    courierRepository.save(courier2);

    List<Courier> availableCouriers = courierRepository.findAllAvailable();

    assertTrue(availableCouriers.stream().anyMatch(c -> c.getId() == courierId1));
    assertFalse(availableCouriers.stream().anyMatch(c -> c.getId() == courierId2));
  }

  @Test
  public void shouldFindOrCreate() {
    long courierId = System.currentTimeMillis();
    transactionTemplate.execute((ts) -> {
      Courier courier = courierRepository.findOrCreateCourier(courierId);
      assertNotNull(courier);
      return null;
    });
    transactionTemplate.execute((ts) -> {
      Courier courier2 = courierRepository.findOrCreateCourier(courierId);
      assertNotNull(courier2);
      return null;
    });
  }

}

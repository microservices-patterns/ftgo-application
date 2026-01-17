package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.flyway.EventuateTramFlywayMigrationConfiguration;
import io.eventuate.tram.sagas.orchestration.*;
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.AccountingServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ConsumerServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.KitchenServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.OrderSagaService;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSaga;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.context.annotation.Lazy;

import java.util.Optional;

@Configuration
@Import({TramEventsPublisherConfiguration.class, SagaOrchestratorConfiguration.class, CommonConfiguration.class, EventuateTramFlywayMigrationConfiguration.class})
public class OrderServiceConfiguration {

  @Bean
  public OrderService orderService(@Lazy SagaInstanceFactory sagaInstanceFactory,
                                   RestaurantRepository restaurantRepository,
                                   OrderRepository orderRepository,
                                   DomainEventPublisher eventPublisher,
                                   CancelOrderSaga cancelOrderSaga,
                                   ReviseOrderSaga reviseOrderSaga,
                                   OrderDomainEventPublisher orderAggregateEventPublisher,
                                   Optional<MeterRegistry> meterRegistry) {

    return new OrderServiceImpl(sagaInstanceFactory, orderRepository, eventPublisher, restaurantRepository,
            cancelOrderSaga, reviseOrderSaga, orderAggregateEventPublisher, meterRegistry);
  }

  @Bean
  public OrderSagaService orderSagaService(OrderRepository orderRepository,
                                           SagaInstanceFactory sagaInstanceFactory,
                                           CreateOrderSaga createOrderSaga) {
    return new OrderSagaService(orderRepository, sagaInstanceFactory, createOrderSaga);
  }

  @Bean
  public CreateOrderSaga createOrderSaga(OrderService orderService, ConsumerServiceProxy consumerService, KitchenServiceProxy kitchenServiceProxy, AccountingServiceProxy accountingService) {
    return new CreateOrderSaga(orderService, consumerService, kitchenServiceProxy, accountingService);
  }

  @Bean
  public CancelOrderSaga cancelOrderSaga() {
    return new CancelOrderSaga();
  }

  @Bean
  public ReviseOrderSaga reviseOrderSaga() {
    return new ReviseOrderSaga();
  }


  @Bean
  public KitchenServiceProxy kitchenServiceProxy() {
    return new KitchenServiceProxy();
  }

  @Bean
  public ConsumerServiceProxy consumerServiceProxy() {
    return new ConsumerServiceProxy();
  }

  @Bean
  public AccountingServiceProxy accountingServiceProxy() {
    return new AccountingServiceProxy();
  }

  @Bean
  public OrderDomainEventPublisher orderAggregateEventPublisher(DomainEventPublisher eventPublisher) {
    return new OrderDomainEventPublisher(eventPublisher);
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}

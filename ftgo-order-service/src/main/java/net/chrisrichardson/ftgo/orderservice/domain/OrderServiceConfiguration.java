package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import io.eventuate.tram.sagas.orchestration.SagaManagerImpl;
import io.eventuate.tram.sagas.orchestration.SagaOrchestratorConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.AccountingServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ConsumerServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.OrderServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.RestaurantOrderServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TramEventsPublisherConfiguration.class, SagaOrchestratorConfiguration.class, CommonConfiguration.class})
public class OrderServiceConfiguration {
  // TODO move to framework

  @Bean
  public SagaCommandProducer sagaCommandProducer() {
    return new SagaCommandProducer();
  }

  @Bean
  public OrderService orderService(RestaurantRepository restaurantRepository, OrderRepository orderRepository, DomainEventPublisher eventPublisher,
                                   SagaManager<CreateOrderSagaData> createOrderSagaManager,
                                   SagaManager<CancelOrderSagaData> cancelOrderSagaManager, SagaManager<ReviseOrderSagaData> reviseOrderSagaManager, OrderDomainEventPublisher orderAggregateEventPublisher) {
    return new OrderService(orderRepository, eventPublisher, restaurantRepository,
            createOrderSagaManager, cancelOrderSagaManager, reviseOrderSagaManager, orderAggregateEventPublisher);
  }

  @Bean
  public SagaManager<CreateOrderSagaData> createOrderSagaManager(CreateOrderSaga saga) {
    return new SagaManagerImpl<>(saga);
  }

  @Bean
  public CreateOrderSaga createOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService, RestaurantOrderServiceProxy restaurantOrderServiceProxy, AccountingServiceProxy accountingService) {
    return new CreateOrderSaga(orderService, consumerService, restaurantOrderServiceProxy, accountingService);
  }

  @Bean
  public SagaManager<CancelOrderSagaData> CancelOrderSagaManager(CancelOrderSaga saga) {
    return new SagaManagerImpl<>(saga);
  }

  @Bean
  public CancelOrderSaga cancelOrderSaga() {
    return new CancelOrderSaga();
  }

  @Bean
  public SagaManager<ReviseOrderSagaData> reviseOrderSagaManager(ReviseOrderSaga saga) {
    return new SagaManagerImpl<>(saga);
  }

  @Bean
  public ReviseOrderSaga reviseOrderSaga() {
    return new ReviseOrderSaga();
  }


  @Bean
  public RestaurantOrderServiceProxy restaurantOrderServiceProxy() {
    return new RestaurantOrderServiceProxy();
  }

  @Bean
  public OrderServiceProxy orderServiceProxy() {
    return new OrderServiceProxy();
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
}

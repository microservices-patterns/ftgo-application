package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.commands.producer.CommandProducer;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.spring.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.sagas.common.SagaLockManager;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import io.eventuate.tram.sagas.orchestration.SagaInstanceRepository;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import io.eventuate.tram.sagas.orchestration.SagaManagerImpl;
import io.eventuate.tram.sagas.orchestration.spring.SagaOrchestratorConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.AccountingServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ConsumerServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.KitchenServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.OrderServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSagaState;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@Configuration
@Import({TramEventsPublisherConfiguration.class, SagaOrchestratorConfiguration.class, CommonConfiguration.class})
public class OrderServiceConfiguration {
  // TODO move to framework

  @Bean
  public SagaCommandProducer sagaCommandProducer(CommandProducer commandProducer) {
    return new SagaCommandProducer(commandProducer);
  }

  @Bean
  public OrderService orderService(RestaurantRepository restaurantRepository, OrderRepository orderRepository, DomainEventPublisher eventPublisher,
                                   SagaManager<CreateOrderSagaState> createOrderSagaManager,
                                   SagaManager<CancelOrderSagaData> cancelOrderSagaManager, SagaManager<ReviseOrderSagaData> reviseOrderSagaManager, OrderDomainEventPublisher orderAggregateEventPublisher, Optional<MeterRegistry> meterRegistry) {
    return new OrderService(orderRepository, eventPublisher, restaurantRepository,
            createOrderSagaManager, cancelOrderSagaManager, reviseOrderSagaManager, orderAggregateEventPublisher, meterRegistry);
  }

  @Bean
  public SagaManager<CreateOrderSagaState> createOrderSagaManager(CreateOrderSaga saga,
                                                                  SagaInstanceRepository sagaInstanceRepository,
                                                                  CommandProducer commandProducer,
                                                                  MessageConsumer messageConsumer,
                                                                  SagaLockManager sagaLockManager,
                                                                  SagaCommandProducer sagaCommandProducer) {
    return new SagaManagerImpl<>(saga, sagaInstanceRepository, commandProducer, messageConsumer, sagaLockManager, sagaCommandProducer);
  }

  @Bean
  public CreateOrderSaga createOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService, KitchenServiceProxy kitchenServiceProxy, AccountingServiceProxy accountingService) {
    return new CreateOrderSaga(orderService, consumerService, kitchenServiceProxy, accountingService);
  }

  @Bean
  public SagaManager<CancelOrderSagaData> CancelOrderSagaManager(CancelOrderSaga saga,
                                                                 SagaInstanceRepository sagaInstanceRepository,
                                                                 CommandProducer commandProducer,
                                                                 MessageConsumer messageConsumer,
                                                                 SagaLockManager sagaLockManager,
                                                                 SagaCommandProducer sagaCommandProducer) {
    return new SagaManagerImpl<>(saga, sagaInstanceRepository, commandProducer, messageConsumer, sagaLockManager, sagaCommandProducer);
  }

  @Bean
  public CancelOrderSaga cancelOrderSaga() {
    return new CancelOrderSaga();
  }

  @Bean
  public SagaManager<ReviseOrderSagaData> reviseOrderSagaManager(ReviseOrderSaga saga,
                                                                 SagaInstanceRepository sagaInstanceRepository,
                                                                 CommandProducer commandProducer,
                                                                 MessageConsumer messageConsumer,
                                                                 SagaLockManager sagaLockManager,
                                                                 SagaCommandProducer sagaCommandProducer) {
    return new SagaManagerImpl<>(saga, sagaInstanceRepository, commandProducer, messageConsumer, sagaLockManager, sagaCommandProducer);
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

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}

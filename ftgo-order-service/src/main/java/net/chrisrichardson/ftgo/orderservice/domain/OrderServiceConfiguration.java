package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import io.eventuate.tram.sagas.orchestration.SagaManagerImpl;
import io.eventuate.tram.sagas.orchestration.SagaOrchestratorConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
@Import({TramEventsPublisherConfiguration.class, SagaOrchestratorConfiguration.class, CommonConfiguration.class})
@ComponentScan
public class OrderServiceConfiguration {

 @Bean
 public OrderService orderService() {
  return new OrderService();
 }

 @Bean
 public SagaManager<CreateOrderSagaData> createOrderSagaManager(CreateOrderSaga saga) {
  return new SagaManagerImpl<>(saga);
 }

 @Bean
 public CreateOrderSaga createOrderSaga() {
  return new CreateOrderSaga();
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



}

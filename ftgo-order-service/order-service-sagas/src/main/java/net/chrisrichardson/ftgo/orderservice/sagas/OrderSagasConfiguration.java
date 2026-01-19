package net.chrisrichardson.ftgo.orderservice.sagas;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRepository;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.AccountingServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ConsumerServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.KitchenServiceProxy;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.OrderSagaService;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSaga;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderSagasConfiguration {

  @Bean
  public OrderSagaService orderSagaService(OrderRepository orderRepository,
                                           SagaInstanceFactory sagaInstanceFactory,
                                           CreateOrderSaga createOrderSaga,
                                           CancelOrderSaga cancelOrderSaga,
                                           ReviseOrderSaga reviseOrderSaga) {
    return new OrderSagaService(orderRepository, sagaInstanceFactory, createOrderSaga, cancelOrderSaga, reviseOrderSaga);
  }

  @Bean
  public CreateOrderSaga createOrderSaga(OrderService orderService,
                                         ConsumerServiceProxy consumerService,
                                         KitchenServiceProxy kitchenServiceProxy,
                                         AccountingServiceProxy accountingService) {
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
}

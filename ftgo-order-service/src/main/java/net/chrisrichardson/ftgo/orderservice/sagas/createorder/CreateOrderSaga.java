package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.*;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrderReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaState> {


  private Logger logger = LoggerFactory.getLogger(getClass());

  private SagaDefinition<CreateOrderSagaState> sagaDefinition;

  public CreateOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService, RestaurantOrderServiceProxy restaurantOrderService,
                         AccountingServiceProxy accountingService) {
    this.sagaDefinition =
             step()
              .withCompensation(orderService.reject, CreateOrderSagaState::makeRejectOrderCommand)
            .step()
              .invokeParticipant(consumerService.validateOrder, CreateOrderSagaState::makeValidateOrderByConsumerCommand)
            .step()
              .invokeParticipant(restaurantOrderService.create, CreateOrderSagaState::makeCreateRestaurantOrderCommand)
              .onReply(CreateRestaurantOrderReply.class, CreateOrderSagaState::handleCreateRestaurantOrderReply)
              .withCompensation(restaurantOrderService.cancel, CreateOrderSagaState::makeCancelCreateRestaurantOrderCommand)
            .step()
              .invokeParticipant(accountingService.authorize, CreateOrderSagaState::makeAuthorizeCommand)
            .step()
              .invokeParticipant(restaurantOrderService.confirmCreate, CreateOrderSagaState::makeConfirmCreateRestaurantOrderCommand)
            .step()
              .invokeParticipant(orderService.approve, CreateOrderSagaState::makeApproveOrderCommand)
            .build();

  }


  @Override
  public SagaDefinition<CreateOrderSagaState> getSagaDefinition() {
    return sagaDefinition;
  }


}

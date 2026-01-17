package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.*;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaState> {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final OrderService orderService;
  private final ConsumerServiceProxy consumerService;
  private final KitchenServiceProxy kitchenService;
  private final AccountingServiceProxy accountingService;

  private SagaDefinition<CreateOrderSagaState> sagaDefinition;

  public CreateOrderSaga(OrderService orderService, ConsumerServiceProxy consumerService,
                         KitchenServiceProxy kitchenService, AccountingServiceProxy accountingService) {
    this.orderService = orderService;
    this.consumerService = consumerService;
    this.kitchenService = kitchenService;
    this.accountingService = accountingService;

    this.sagaDefinition =
             step()
              .invokeLocal(this::create)
              .withCompensation(this::reject)
            .step()
              .invokeParticipant(consumerService.validateOrder, CreateOrderSagaState::makeValidateOrderByConsumerCommand)
            .step()
              .invokeParticipant(kitchenService.create, CreateOrderSagaState::makeCreateTicketCommand)
              .onReply(CreateTicketReply.class, CreateOrderSagaState::handleCreateTicketReply)
              .withCompensation(kitchenService.cancel, CreateOrderSagaState::makeCancelCreateTicketCommand)
            .step()
                .invokeParticipant(accountingService.authorize, CreateOrderSagaState::makeAuthorizeCommand)
            .step()
              .invokeParticipant(kitchenService.confirmCreate, CreateOrderSagaState::makeConfirmCreateTicketCommand)
            .step()
              .invokeLocal(this::approve)
            .build();
  }

  @Override
  public List<Object> getParticipantProxies() {
    return List.of(consumerService, kitchenService, accountingService);
  }

  @Override
  public SagaDefinition<CreateOrderSagaState> getSagaDefinition() {
    return sagaDefinition;
  }

  private void create(CreateOrderSagaState data) {
    Order order = orderService.createOrder(
            data.getConsumerId(),
            data.getRestaurantId(),
            data.getDeliveryInformation(),
            data.getLineItems());
    data.setOrderId(order.getId());
    data.setOrderDetails(makeOrderDetails(order));
  }

  private OrderDetails makeOrderDetails(Order order) {
    List<OrderLineItem> orderLineItems = order.getLineItems().stream()
            .map(li -> new OrderLineItem(li.getMenuItemId(), li.getName(), li.getPrice(), li.getQuantity()))
            .toList();
    return new OrderDetails(
            order.getConsumerId(),
            order.getRestaurantId(),
            orderLineItems,
            order.getOrderTotal());
  }

  private void approve(CreateOrderSagaState data) {
    orderService.approveOrder(data.getOrderId());
  }

  private void reject(CreateOrderSagaState data) {
    orderService.rejectOrder(data.getOrderId());
  }
}

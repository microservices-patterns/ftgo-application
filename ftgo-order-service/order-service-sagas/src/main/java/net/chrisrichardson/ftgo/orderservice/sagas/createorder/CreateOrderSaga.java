package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.*;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import net.chrisrichardson.ftgo.kitchenservice.api.TicketDetails;
import net.chrisrichardson.ftgo.kitchenservice.api.TicketLineItem;
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
              .invokeParticipant(this::validateConsumer)
            .step()
              .invokeParticipant(this::createTicket)
              .onReply(CreateTicketReply.class, CreateOrderSagaState::handleCreateTicketReply)
              .withCompensation(this::cancelTicket)
            .step()
              .invokeParticipant(this::authorizePayment)
            .step()
              .invokeParticipant(this::confirmTicket)
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

  private CommandWithDestination validateConsumer(CreateOrderSagaState data) {
    OrderDetails orderDetails = data.getOrderDetails();
    return consumerService.validateOrder(
            orderDetails.getConsumerId(),
            data.getOrderId(),
            orderDetails.getOrderTotal());
  }

  private CommandWithDestination createTicket(CreateOrderSagaState data) {
    OrderDetails orderDetails = data.getOrderDetails();
    TicketDetails ticketDetails = makeTicketDetails(orderDetails);
    return kitchenService.createTicket(
            orderDetails.getRestaurantId(),
            data.getOrderId(),
            ticketDetails);
  }

  private TicketDetails makeTicketDetails(OrderDetails orderDetails) {
    List<TicketLineItem> ticketLineItems = orderDetails.getLineItems().stream()
            .map(li -> new TicketLineItem(li.getMenuItemId(), li.getName(), li.getQuantity()))
            .toList();
    return new TicketDetails(ticketLineItems);
  }

  private CommandWithDestination cancelTicket(CreateOrderSagaState data) {
    return kitchenService.cancelCreateTicket(data.getOrderId());
  }

  private CommandWithDestination authorizePayment(CreateOrderSagaState data) {
    OrderDetails orderDetails = data.getOrderDetails();
    return accountingService.authorize(
            orderDetails.getConsumerId(),
            data.getOrderId(),
            orderDetails.getOrderTotal());
  }

  private CommandWithDestination confirmTicket(CreateOrderSagaState data) {
    return kitchenService.confirmCreateTicket(data.getTicketId());
  }
}

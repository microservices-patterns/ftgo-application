package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDomainEvent;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSagaState;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Transactional
public class OrderService {

  public Order createOrder(long consumerId, long restaurantId,
                           List<MenuItemIdAndQuantity> lineItems) {
    Restaurant restaurant = restaurantRepository.findById(restaurantId).get();

    List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant);

    ResultWithDomainEvents<Order, OrderDomainEvent> orderAndEvents =
            Order.createOrder(consumerId, restaurantId, orderLineItems);

    Order order = orderAndEvents.result;
    orderRepository.save(order);

    orderAggregateEventPublisher.publish(order, orderAndEvents.events);

    OrderDetails orderDetails = new OrderDetails(consumerId, restaurantId, orderLineItems, order.getOrderTotal());

    CreateOrderSagaState data = new CreateOrderSagaState(order.getId(), orderDetails);
    createOrderSagaManager.create(data, Order.class, order.getId());

    meterRegistry.counter("placed_orders").increment();

    return order;
  }


  private Logger logger = LoggerFactory.getLogger(getClass());

  private OrderRepository orderRepository;

  private RestaurantRepository restaurantRepository;

  private SagaManager<CreateOrderSagaState> createOrderSagaManager;

  private SagaManager<CancelOrderSagaData> cancelOrderSagaManager;
  
  private SagaManager<ReviseOrderSagaData> reviseOrderSagaManager;

  private OrderDomainEventPublisher orderAggregateEventPublisher;

  @Autowired
  private MeterRegistry meterRegistry;

  public OrderService(OrderRepository orderRepository, DomainEventPublisher eventPublisher, RestaurantRepository restaurantRepository, SagaManager<CreateOrderSagaState> createOrderSagaManager, SagaManager<CancelOrderSagaData> cancelOrderSagaManager, SagaManager<ReviseOrderSagaData> reviseOrderSagaManager, OrderDomainEventPublisher orderAggregateEventPublisher) {
    this.orderRepository = orderRepository;
    this.restaurantRepository = restaurantRepository;
    this.createOrderSagaManager = createOrderSagaManager;
    this.cancelOrderSagaManager = cancelOrderSagaManager;
    this.reviseOrderSagaManager = reviseOrderSagaManager;
    this.orderAggregateEventPublisher = orderAggregateEventPublisher;
  }

  private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, Restaurant restaurant) {
    return lineItems.stream().map(li -> {
        MenuItem om = restaurant.findMenuItem(li.getMenuItemId()).orElseThrow(() -> new RuntimeException("MenuItem not found: " + li.getMenuItemId()));
        return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
      }).collect(toList());
  }


//  public Order reviseOrder(Long orderId, Long expectedVersion, OrderRevision orderRevision)  {
//
//    ReviseOrderSagaData sagaData = new ReviseOrderSagaData(orderId, expectedVersion, orderRevision);
//    reviseOrderSagaManager.create(sagaData);
//
//    return orderRepository.findById(orderId);
//  }

  public Optional<Order> confirmChangeLineItemQuantity(Long orderId, OrderRevision orderRevision)  {
    return orderRepository.findById(orderId).map(order -> {
      List<OrderDomainEvent> events = order.confirmRevision(orderRevision);
      orderAggregateEventPublisher.publish(order, events);
      return order;
    });
  }

  public void noteReversingAuthorization(Long orderId) {
    throw new UnsupportedOperationException();
  }

  public Optional<Order> cancel(Long orderId) {
    return orderRepository.findById(orderId).map(order -> {
      CancelOrderSagaData sagaData = new CancelOrderSagaData(order.getConsumerId(), orderId, order.getOrderTotal());
      cancelOrderSagaManager.create(sagaData);
      return order;
    });
  }

  private Optional<Order> updateOrder(long orderId, Function<Order, List<OrderDomainEvent>> updater) {
    return orderRepository.findById(orderId).map(order -> {
      orderAggregateEventPublisher.publish(order, updater.apply(order));
      return order;
    });
  }

  public void approveOrder(long orderId) {
    updateOrder(orderId, Order::noteApproved).orElseThrow(RuntimeException::new);
    meterRegistry.counter("approved_orders").increment();
  }

  public void rejectOrder(long orderId) {
    updateOrder(orderId, Order::noteRejected).orElseThrow(RuntimeException::new);
    meterRegistry.counter("rejected_orders").increment();
  }

  public void beginCancel(long orderId) {
    updateOrder(orderId, Order::cancel).orElseThrow(RuntimeException::new);
  }

  public void undoCancel(long orderId) {
    updateOrder(orderId, Order::undoPendingCancel);
  }

  public void confirmCancelled(long orderId) {
    updateOrder(orderId, Order::noteCancelled);
  }

  public Optional<Order> reviseOrder(long orderId, OrderRevision orderRevision) {
    return orderRepository.findById(orderId).map(order -> {
      ReviseOrderSagaData sagaData = new ReviseOrderSagaData(order.getConsumerId(), orderId, null, orderRevision);
      reviseOrderSagaManager.create(sagaData);
      return order;
    });
  }

  public Optional<RevisedOrder> beginReviseOrder(long orderId, OrderRevision revision) {
    return orderRepository.findById(orderId).map(order -> {
      ResultWithDomainEvents<LineItemQuantityChange, OrderDomainEvent> result = order.revise(revision);
      orderAggregateEventPublisher.publish(order, result.events);
      return new RevisedOrder(order, result.result);
    });
  }

  public void undoPendingRevision(long orderId) {
    updateOrder(orderId, Order::rejectRevision);
  }

  public void confirmRevision(long orderId, OrderRevision revision) {
    updateOrder(orderId, order -> order.confirmRevision(revision));
  }

  public void createMenu(long id, RestaurantMenu menu) {
    System.out.println("menu=" + menu.getMenuItems());
    Restaurant restaurant = new Restaurant(id, menu.getMenuItems());
    restaurantRepository.save(restaurant);
    System.out.println("menu=" + restaurant.getMenuItems());
  }

  public void reviseMenu(long id, RestaurantMenu revisedMenu) {
    restaurantRepository.findById(id).map(restaurant -> {
      List<OrderDomainEvent> events = restaurant.reviseMenu(revisedMenu);
      return restaurant;
    }).orElseThrow(RuntimeException::new);
  }

}

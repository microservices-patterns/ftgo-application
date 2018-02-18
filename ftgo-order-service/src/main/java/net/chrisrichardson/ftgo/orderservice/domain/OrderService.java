package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.ResultWithEvents;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDomainEvent;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Transactional
public class OrderService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private OrderRepository orderRepository;

  private RestaurantRepository restaurantRepository;

  private SagaManager<CreateOrderSagaData> createOrderSagaManager;

  private SagaManager<CancelOrderSagaData> cancelOrderSagaManager;
  
  private SagaManager<ReviseOrderSagaData> reviseOrderSagaManager;

  private OrderDomainEventPublisher orderAggregateEventPublisher;

  public OrderService(OrderRepository orderRepository, DomainEventPublisher eventPublisher, RestaurantRepository restaurantRepository, SagaManager<CreateOrderSagaData> createOrderSagaManager, SagaManager<CancelOrderSagaData> cancelOrderSagaManager, SagaManager<ReviseOrderSagaData> reviseOrderSagaManager, OrderDomainEventPublisher orderAggregateEventPublisher) {
    this.orderRepository = orderRepository;
    this.restaurantRepository = restaurantRepository;
    this.createOrderSagaManager = createOrderSagaManager;
    this.cancelOrderSagaManager = cancelOrderSagaManager;
    this.reviseOrderSagaManager = reviseOrderSagaManager;
    this.orderAggregateEventPublisher = orderAggregateEventPublisher;
  }

  public Order createOrder(long consumerId, long restaurantId, List<MenuItemIdAndQuantity> lineItems) {
    Restaurant restaurant = restaurantRepository.findOne(restaurantId);
    if (restaurant == null)
      throw new RuntimeException("Restaurant not found: " + restaurantId);

    List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant);
    ResultWithDomainEvents<Order, OrderDomainEvent> orderAndEvents = Order.createOrder(consumerId, restaurantId, orderLineItems);
    Order order = orderAndEvents.result;
    orderRepository.save(order);

    orderAggregateEventPublisher.publish(order, orderAndEvents.events);

    OrderDetails orderDetails = new OrderDetails(consumerId, restaurantId, orderLineItems, order.getOrderTotal());
    CreateOrderSagaData data = new CreateOrderSagaData(order.getId(), orderDetails);
    createOrderSagaManager.create(data, Order.class, order.getId());

    return order;
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
//    return orderRepository.findOne(orderId);
//  }

  public Order confirmChangeLineItemQuantity(Long orderId, OrderRevision orderRevision)  {
    Order order = orderRepository.findOne(orderId);
    List<OrderDomainEvent> events = order.confirmRevision(orderRevision);
    orderAggregateEventPublisher.publish(order, events);
    return order;
  }

  public void noteReversingAuthorization(Long orderId) {
    throw new UnsupportedOperationException();
  }

  public Order cancel(Long orderId) {
    Order order = orderRepository.findOne(orderId);
    if (order == null) {
      logger.error("Cannot find order: {}", orderId);
      return null;
    }
    CancelOrderSagaData sagaData = new CancelOrderSagaData(order.getConsumerId(), orderId, order.getOrderTotal());
    cancelOrderSagaManager.create(sagaData);
    return order;
  }

  Order updateOrder(long orderId, Function<Order, List<OrderDomainEvent>> updater) {
    Order order = orderRepository.findOne(orderId);
    orderAggregateEventPublisher.publish(order, updater.apply(order));
    return order;
  }

  public void approveOrder(long orderId) {
    updateOrder(orderId, Order::noteAuthorized);
  }

  public void rejectOrder(long orderId) {
    updateOrder(orderId, Order::noteRejected);
  }

  public void beginCancel(long orderId) {
    updateOrder(orderId, Order::cancel);
  }

  public void undoCancel(long orderId) {
    updateOrder(orderId, Order::undoPendingCancel);
  }

  public void confirmCancelled(long orderId) {
    updateOrder(orderId, Order::noteCancelled);
  }

  public Order reviseOrder(long orderId, OrderRevision orderRevision) {
    Order order = orderRepository.findOne(orderId);
    if (order == null) {
      logger.error("Cannot find order: {}", orderId);
      return null;
    }
    ReviseOrderSagaData sagaData = new ReviseOrderSagaData(order.getConsumerId(), orderId, null, orderRevision);
    reviseOrderSagaManager.create(sagaData);
    return order;
  }

  public RevisedOrder beginReviseOrder(long orderId, OrderRevision revision) {
    Order order = orderRepository.findOne(orderId);
    ResultWithDomainEvents<LineItemQuantityChange, OrderDomainEvent> result = order.revise(revision);
    orderAggregateEventPublisher.publish(order, result.events);
    return new RevisedOrder(order, result.result);
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
    Restaurant restaurant = restaurantRepository.findOne(id);
    List<OrderDomainEvent> events = restaurant.reviseMenu(revisedMenu);
  }

}

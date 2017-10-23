package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.ResultWithEvents;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Transactional
public class OrderService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private DomainEventPublisher eventPublisher;

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private SagaManager<CreateOrderSagaData> createOrderSagaManager;

  @Autowired
  private SagaManager<CancelOrderSagaData> cancelOrderSagaManager;
  

  @Autowired
  private SagaManager<ReviseOrderSagaData> reviseOrderSagaManager;


  public Order createOrder(long consumerId, long restaurantId, List<MenuItemIdAndQuantity> lineItems) {
    Restaurant restaurant = restaurantRepository.findOne(restaurantId);
    if (restaurant == null)
      throw new RuntimeException("Restaurant not found: " + restaurantId);

    System.out.println("restaurant=" + restaurant.getMenuItems());

    List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant);
    ResultWithEvents<Order> orderAndEvents = Order.createOrder(consumerId, restaurantId, orderLineItems);
    Order order = orderAndEvents.result;
    orderRepository.save(order);

    eventPublisher.publish(Order.class, order.getId(), orderAndEvents.events);

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
    List<DomainEvent> events = order.confirmRevision(orderRevision);
    eventPublisher.publish(Order.class, Long.toString(order.getId()), events);
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

  Order updateOrder(long orderId, Function<Order, List<DomainEvent>> updater) {
    Order order = orderRepository.findOne(orderId);
    eventPublisher.publish(Order.class, Long.toString(orderId), updater.apply(order));
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
    Order order1 = orderRepository.findOne(orderId);
    ResultWithEvents<LineItemQuantityChange> result = order1.revise(revision);
    eventPublisher.publish(Order.class, Long.toString(orderId), result.events);
    return new RevisedOrder(order1, result.result);
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
    List<DomainEvent> events = restaurant.reviseMenu(revisedMenu);
  }

}

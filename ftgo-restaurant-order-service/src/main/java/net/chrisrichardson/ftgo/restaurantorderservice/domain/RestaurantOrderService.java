package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import io.eventuate.tram.events.ResultWithEvents;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Transactional
public class RestaurantOrderService {

  @Autowired
  private RestaurantOrderRepository restaurantOrderRepository;

  @Autowired
  private DomainEventPublisher domainEventPublisher;



  public void accept(long orderId, LocalDateTime readyBy) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    List<DomainEvent> events = restaurantOrder.accept(readyBy);
    domainEventPublisher.publish(RestaurantOrder.class, orderId, events);
  }

  public RestaurantOrder createRestaurantOrder(long restaurantId, Long restaurantOrderId, RestaurantOrderDetails restaurantOrderDetails) {
    ResultWithEvents<RestaurantOrder> rwe = RestaurantOrder.create(restaurantId, restaurantOrderId, restaurantOrderDetails);
    restaurantOrderRepository.save(rwe.result);
    domainEventPublisher.publish(RestaurantOrder.class, rwe.result.getId(), rwe.events);
    return rwe.result;
  }

  public void confirmCreateRestaurantOrder(Long restaurantOrderId) {
    RestaurantOrder ro = restaurantOrderRepository.findOne(restaurantOrderId);
    List<DomainEvent> events = ro.confirmCreate();
    domainEventPublisher.publish(RestaurantOrder.class, restaurantOrderId, events);
  }

  public void cancelCreateRestaurantOrder(Long restaurantOrderId) {
    RestaurantOrder ro = restaurantOrderRepository.findOne(restaurantOrderId);
    List<DomainEvent> events = ro.cancelCreate();
    domainEventPublisher.publish(RestaurantOrder.class, restaurantOrderId, events);
  }


  public void cancelRestaurantOrder(long restaurantId, long orderId) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<DomainEvent> events = restaurantOrder.cancel();
    domainEventPublisher.publish(RestaurantOrder.class, orderId, events);
  }


  public void confirmCancelRestaurantOrder(long restaurantId, long orderId) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<DomainEvent> events = restaurantOrder.confirmCancel();
    domainEventPublisher.publish(RestaurantOrder.class, orderId, events);
  }

  public void undoCancel(long restaurantId, long orderId) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<DomainEvent> events = restaurantOrder.undoCancel();
    domainEventPublisher.publish(RestaurantOrder.class, orderId, events);

  }

  public void beginReviseOrder(long restaurantId, Long orderId, Map<String, Integer> revisedLineItemQuantities) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<DomainEvent> events = restaurantOrder.beginReviseOrder(revisedLineItemQuantities);
    domainEventPublisher.publish(RestaurantOrder.class, orderId, events);

  }

  public void undoBeginReviseOrder(long restaurantId, Long orderId) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<DomainEvent> events = restaurantOrder.undoBeginReviseOrder();
    domainEventPublisher.publish(RestaurantOrder.class, orderId, events);
  }

  public void confirmReviseRestaurantOrder(long restaurantId, long orderId, Map<String, Integer> revisedLineItemQuantities) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<DomainEvent> events = restaurantOrder.confirmReviseRestaurantOrder(revisedLineItemQuantities);
    domainEventPublisher.publish(RestaurantOrder.class, orderId, events);
  }


  // ...
}

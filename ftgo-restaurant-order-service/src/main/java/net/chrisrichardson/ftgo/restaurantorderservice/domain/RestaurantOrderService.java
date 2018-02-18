package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import io.eventuate.tram.events.ResultWithEvents;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.events.common.DomainEvent;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;
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
  private RestaurantOrderDomainEventPublisher domainEventPublisher;


  public RestaurantOrder createRestaurantOrder(long restaurantId, Long restaurantOrderId, RestaurantOrderDetails restaurantOrderDetails) {
    ResultWithDomainEvents<RestaurantOrder, RestaurantOrderDomainEvent> rwe = RestaurantOrder.create(restaurantId, restaurantOrderId, restaurantOrderDetails);
    restaurantOrderRepository.save(rwe.result);
    domainEventPublisher.publish(rwe.result, rwe.events);
    return rwe.result;
  }

  public void accept(long orderId, LocalDateTime readyBy) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    List<RestaurantOrderDomainEvent> events = restaurantOrder.accept(readyBy);
    domainEventPublisher.publish(restaurantOrder, events);
  }

  public void confirmCreateRestaurantOrder(Long restaurantOrderId) {
    RestaurantOrder ro = restaurantOrderRepository.findOne(restaurantOrderId);
    List<RestaurantOrderDomainEvent> events = ro.confirmCreate();
    domainEventPublisher.publish(ro, events);
  }

  public void cancelCreateRestaurantOrder(Long restaurantOrderId) {
    RestaurantOrder ro = restaurantOrderRepository.findOne(restaurantOrderId);
    List<RestaurantOrderDomainEvent> events = ro.cancelCreate();
    domainEventPublisher.publish(ro, events);
  }


  public void cancelRestaurantOrder(long restaurantId, long orderId) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<RestaurantOrderDomainEvent> events = restaurantOrder.cancel();
    domainEventPublisher.publish(restaurantOrder, events);
  }


  public void confirmCancelRestaurantOrder(long restaurantId, long orderId) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<RestaurantOrderDomainEvent> events = restaurantOrder.confirmCancel();
    domainEventPublisher.publish(restaurantOrder, events);
  }

  public void undoCancel(long restaurantId, long orderId) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<RestaurantOrderDomainEvent> events = restaurantOrder.undoCancel();
    domainEventPublisher.publish(restaurantOrder, events);

  }

  public void beginReviseOrder(long restaurantId, Long orderId, Map<String, Integer> revisedLineItemQuantities) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<RestaurantOrderDomainEvent> events = restaurantOrder.beginReviseOrder(revisedLineItemQuantities);
    domainEventPublisher.publish(restaurantOrder, events);

  }

  public void undoBeginReviseOrder(long restaurantId, Long orderId) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<RestaurantOrderDomainEvent> events = restaurantOrder.undoBeginReviseOrder();
    domainEventPublisher.publish(restaurantOrder, events);
  }

  public void confirmReviseRestaurantOrder(long restaurantId, long orderId, Map<String, Integer> revisedLineItemQuantities) {
    RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(orderId);
    // TODO - verify restaurant id
    List<RestaurantOrderDomainEvent> events = restaurantOrder.confirmReviseRestaurantOrder(revisedLineItemQuantities);
    domainEventPublisher.publish(restaurantOrder, events);
  }


  // ...
}

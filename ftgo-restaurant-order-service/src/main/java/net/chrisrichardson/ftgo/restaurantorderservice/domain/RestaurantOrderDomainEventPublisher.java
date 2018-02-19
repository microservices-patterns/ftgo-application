package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;

public class RestaurantOrderDomainEventPublisher extends AbstractAggregateDomainEventPublisher<RestaurantOrder, RestaurantOrderDomainEvent> {

  public RestaurantOrderDomainEventPublisher(DomainEventPublisher eventPublisher) {
    super(eventPublisher, RestaurantOrder.class, RestaurantOrder::getId);
  }

}

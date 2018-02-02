package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderCreatedEvent;

public class OrderAggregateEventPublisher extends AbstractAggregateEventPublisher<Order> {


  public OrderAggregateEventPublisher(DomainEventPublisher eventPublisher) {
    super(eventPublisher, Order.class, Order::getId,
            OrderCreatedEvent.class,
            OrderAuthorized.class,
            OrderRejected.class,
            OrderRevisionProposed.class,
            OrderRevised.class,
            OrderRevisionRejected.class,
            OrderCancelled.class);
  }

}

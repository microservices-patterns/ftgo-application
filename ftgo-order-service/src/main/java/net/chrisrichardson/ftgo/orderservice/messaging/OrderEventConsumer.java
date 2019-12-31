package net.chrisrichardson.ftgo.orderservice.messaging;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuRevised;


public class OrderEventConsumer {

  private OrderService orderService;

  public OrderEventConsumer(OrderService orderService) {
    this.orderService = orderService;
  }

  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder
            .forAggregateType("net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant")
            .onEvent(RestaurantCreated.class, this::createMenu)
            .onEvent(RestaurantMenuRevised.class, this::reviseMenu)
            .build();
  }

  private void createMenu(DomainEventEnvelope<RestaurantCreated> de) {
    String restaurantIds = de.getAggregateId();
    long id = Long.parseLong(restaurantIds);
    orderService.createMenu(id, de.getEvent().getName(), RestaurantEventMapper.toMenuItems(de.getEvent().getMenu().getMenuItems()));
  }

  public void reviseMenu(DomainEventEnvelope<RestaurantMenuRevised> de) {
    String restaurantIds = de.getAggregateId();
    long id = Long.parseLong(restaurantIds);
    orderService.reviseMenu(id, RestaurantEventMapper.toMenuItems(de.getEvent().getMenu().getMenuItems()));
  }

}

package net.chrisrichardson.ftgo.orderservice.messaging;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuRevised;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


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
    RestaurantMenu menu = de.getEvent().getMenu();
    orderService.createMenu(id, de.getEvent().getName(), menu);
  }

  public void reviseMenu(DomainEventEnvelope<RestaurantMenuRevised> de) {

    String restaurantIds = de.getAggregateId();
    long id = Long.parseLong(restaurantIds);
    RestaurantMenu revisedMenu = de.getEvent().getRevisedMenu();

    orderService.reviseMenu(id, revisedMenu);
  }

}

package net.chrisrichardson.ftgo.kitchenservice.messagehandlers;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import net.chrisrichardson.ftgo.kitchenservice.domain.KitchenService;
import net.chrisrichardson.ftgo.kitchenservice.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuRevised;
import org.springframework.beans.factory.annotation.Autowired;


public class KitchenServiceEventConsumer {

  @Autowired
  private KitchenService kitchenService;

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
    RestaurantMenu menu = new RestaurantMenu(RestaurantEventMapper.toMenuItems(de.getEvent().getMenu().getMenuItems()));
    kitchenService.createMenu(id, menu);
  }

  public void reviseMenu(DomainEventEnvelope<RestaurantMenuRevised> de) {
    long id = Long.parseLong(de.getAggregateId());
    RestaurantMenu menu = new RestaurantMenu(RestaurantEventMapper.toMenuItems(de.getEvent().getMenu().getMenuItems()));
    kitchenService.reviseMenu(id, menu);
  }

}

package net.chrisrichardson.ftgo.restaurantorderservice.messagehandlers;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantService;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuRevised;
import org.springframework.beans.factory.annotation.Autowired;


public class RestaurantOrderEventConsumer {

  @Autowired
  private RestaurantService restaurantService;

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
    restaurantService.createMenu(id, menu);
  }

  public void reviseMenu(DomainEventEnvelope<RestaurantMenuRevised> de) {

    long id = Long.parseLong(de.getAggregateId());
    RestaurantMenu revisedMenu = de.getEvent().getRevisedMenu();
    restaurantService.reviseMenu(id, revisedMenu);
  }

}

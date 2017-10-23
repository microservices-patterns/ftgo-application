package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class RestaurantService {

  @Autowired
  private DomainEventPublisher domainEventPublisher;

  @Autowired
  private RestaurantRepository restaurantRepository;

  public void reviseMenu(long id, RestaurantMenu revisedMenu) {
    Restaurant restaurant = restaurantRepository.findOne(id);
    List<DomainEvent> events = restaurant.reviseMenu(revisedMenu);
    domainEventPublisher.publish(RestaurantOrder.class, id, events);
  }

  public void createMenu(long id, RestaurantMenu menu) {
    Restaurant restaurant = new Restaurant(id, menu.getMenuItems());
    restaurantRepository.save(restaurant);
  }

}

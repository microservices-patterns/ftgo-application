package net.chrisrichardson.ftgo.restaurantservice.domain;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import net.chrisrichardson.ftgo.restaurantservice.events.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Transactional
public class RestaurantService {


  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private DomainEventPublisher domainEventPublisher;

  public Restaurant create(CreateRestaurantRequest request) {
    Restaurant restaurant = new Restaurant(request.getName(), request.getMenu());
    restaurantRepository.save(restaurant);
    domainEventPublisher.publish(Restaurant.class, restaurant.getId(), Collections.singletonList(new RestaurantCreated(request.getName(), request.getMenu())));
    return restaurant;
  }
}

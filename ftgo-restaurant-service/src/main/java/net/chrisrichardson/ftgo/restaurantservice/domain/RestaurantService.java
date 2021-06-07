package net.chrisrichardson.ftgo.restaurantservice.domain;

import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional
public class RestaurantService {


  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private RestaurantDomainEventPublisher restaurantDomainEventPublisher;

  public Restaurant create(CreateRestaurantRequest request) {
    Restaurant restaurant = new Restaurant(request.getName(), request.getMenu(), request.getEfficiency());
    restaurantRepository.save(restaurant);
    restaurantDomainEventPublisher.publish(restaurant, Collections.singletonList(new RestaurantCreated(request.getName(), request.getAddress(), request.getMenu(), request.getEfficiency())));
    return restaurant;
  }

  public Optional<Restaurant> findById(long restaurantId) {
    return restaurantRepository.findById(restaurantId);
  }

  public List<Restaurant> getAllRestaurants() {
    return (List<Restaurant>) restaurantRepository.findAll();
  }
}

package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import org.springframework.data.repository.CrudRepository;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
}

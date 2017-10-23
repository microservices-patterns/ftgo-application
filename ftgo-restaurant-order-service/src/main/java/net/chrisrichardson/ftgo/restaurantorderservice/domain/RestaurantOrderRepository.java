package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import org.springframework.data.repository.CrudRepository;

public interface RestaurantOrderRepository extends CrudRepository<RestaurantOrder, Long> {
}

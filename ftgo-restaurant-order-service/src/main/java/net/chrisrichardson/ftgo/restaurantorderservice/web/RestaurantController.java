package net.chrisrichardson.ftgo.restaurantorderservice.web;

import net.chrisrichardson.ftgo.restaurantorderservice.domain.Restaurant;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/restaurants")
public class RestaurantController {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @RequestMapping(path = "/{restaurantId}", method = RequestMethod.GET)
  public ResponseEntity<GetRestaurantResponse> getRestaurant(@PathVariable long restaurantId) {
    Restaurant restaurant = restaurantRepository.findOne(restaurantId);
    if (restaurant == null)
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(new GetRestaurantResponse(restaurantId), HttpStatus.OK);

  }
}

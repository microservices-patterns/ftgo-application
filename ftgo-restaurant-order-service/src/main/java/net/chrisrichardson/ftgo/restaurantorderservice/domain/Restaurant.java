package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenu;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "restaurant_order_service_restaurants")
@Access(AccessType.FIELD)
public class Restaurant {

  @Id
  private Long id;

  @Embedded
  @ElementCollection
  @CollectionTable(name = "restaurant_order_service_restaurant_menu_items")
  private List<MenuItem> menuItems;

  private Restaurant() {
  }

  public Restaurant(long id, List<MenuItem> menuItems) {
    this.id = id;
    this.menuItems = menuItems;
  }

  public List<DomainEvent> reviseMenu(RestaurantMenu revisedMenu) {
    throw new UnsupportedOperationException();
  }

  public void verifyRestaurantDetails(RestaurantOrderDetails restaurantOrderDetails) {
    // TODO - implement me
  }

  public Long getId() {
    return id;
  }

}

package net.chrisrichardson.ftgo.restaurantservice.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "restaurants")
@Access(AccessType.FIELD)
public class Restaurant {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private Long efficiency;

  @Embedded
  private RestaurantMenu menu;

  private Restaurant() {
  }

  public Restaurant(String name, RestaurantMenu menu, Long efficiency) {
    this.name = name;
    this.menu = menu;
    this.efficiency = efficiency;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public RestaurantMenu getMenu() {
    return menu;
  }

  public Long getEfficiency() {
    return efficiency;
  }

  public void setEfficiency(Long efficiency) {
    this.efficiency = efficiency;
  }

}

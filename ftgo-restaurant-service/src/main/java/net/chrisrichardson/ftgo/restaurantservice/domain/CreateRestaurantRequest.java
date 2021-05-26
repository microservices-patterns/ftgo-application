package net.chrisrichardson.ftgo.restaurantservice.domain;

import net.chrisrichardson.ftgo.common.Address;

public class CreateRestaurantRequest {

  private String name;
  private Address address;
  private RestaurantMenu menu;
  private Long efficiency; // how many orders can be processed within an hour

  private CreateRestaurantRequest() {

  }

  public CreateRestaurantRequest(String name, Address address, RestaurantMenu menu, Long efficiency) {
    this.name = name;
    this.address = address;
    this.menu = menu;
    this.efficiency = efficiency;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RestaurantMenu getMenu() {
    return menu;
  }

  public void setMenu(RestaurantMenu menu) {
    this.menu = menu;
  }

  public Address getAddress() {
    return address;
  }

  public Long getEfficiency() {
    return efficiency;
  }

  public void setEfficiency(Long efficiency) {
    this.efficiency = efficiency;
  }

}

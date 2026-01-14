package net.chrisrichardson.ftgo.restaurantservice.events;

import net.chrisrichardson.ftgo.common.Address;

public class RestaurantCreated implements RestaurantDomainEvent {
  private String name;
  private Address address;

  public String getName() {
    return name;
  }

  private RestaurantCreated() {
  }

  public RestaurantCreated(String name, Address address) {
    this.name = name;
    this.address = address;
    if (address == null)
      throw new NullPointerException("Null address");
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
}

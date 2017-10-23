package net.chrisrichardson.ftgo.restaurantorderservice.api;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class RestaurantOrderLineItem {

  private int quantity;
  private String menuItemId;
  private String name;


}

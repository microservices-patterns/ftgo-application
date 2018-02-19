package net.chrisrichardson.ftgo.restaurantorderservice.api;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class RestaurantOrderDetails {
  private List<RestaurantOrderLineItem> lineItems;

  public RestaurantOrderDetails() {
  }

  public RestaurantOrderDetails(List<RestaurantOrderLineItem> lineItems) {
    this.lineItems = lineItems;
  }

  public List<RestaurantOrderLineItem> getLineItems() {
    return lineItems;
  }

  public void setLineItems(List<RestaurantOrderLineItem> lineItems) {
    this.lineItems = lineItems;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}

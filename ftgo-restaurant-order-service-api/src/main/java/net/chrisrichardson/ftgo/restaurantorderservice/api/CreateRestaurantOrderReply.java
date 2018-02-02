package net.chrisrichardson.ftgo.restaurantorderservice.api;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CreateRestaurantOrderReply {
  private long restaurantOrderId;

  private CreateRestaurantOrderReply() {
  }

  public CreateRestaurantOrderReply(long restaurantOrderId) {

    this.restaurantOrderId = restaurantOrderId;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  public void setRestaurantOrderId(long restaurantOrderId) {
    this.restaurantOrderId = restaurantOrderId;
  }

  public long getRestaurantOrderId() {
    return restaurantOrderId;
  }
}

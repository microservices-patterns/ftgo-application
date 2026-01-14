package net.chrisrichardson.ftgo.orderservice.api.events;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class OrderDetails {

  private long restaurantId;
  private long consumerId;

  private OrderDetails() {
  }

  public OrderDetails(long consumerId, long restaurantId) {
    this.consumerId = consumerId;
    this.restaurantId = restaurantId;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public long getConsumerId() {
    return consumerId;
  }

  public void setRestaurantId(long restaurantId) {
    this.restaurantId = restaurantId;
  }

  public void setConsumerId(long consumerId) {
    this.consumerId = consumerId;
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}

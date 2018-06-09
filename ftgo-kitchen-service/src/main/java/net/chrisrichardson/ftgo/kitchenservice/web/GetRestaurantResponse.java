package net.chrisrichardson.ftgo.kitchenservice.web;

public class GetRestaurantResponse  {
  private long restaurantId;

  public long getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(long restaurantId) {
    this.restaurantId = restaurantId;
  }

  public GetRestaurantResponse() {

  }

  public GetRestaurantResponse(long restaurantId) {
    this.restaurantId = restaurantId;
  }
}

package net.chrisrichardson.ftgo.consumerservice.domain.swagger;

public class GetRestaurantResponse {
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

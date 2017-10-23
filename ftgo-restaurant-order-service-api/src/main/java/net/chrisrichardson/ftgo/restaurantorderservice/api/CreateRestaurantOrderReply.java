package net.chrisrichardson.ftgo.restaurantorderservice.api;

public class CreateRestaurantOrderReply {
  private long restaurantOrderId;

  private CreateRestaurantOrderReply() {
  }

  public void setRestaurantOrderId(long restaurantOrderId) {
    this.restaurantOrderId = restaurantOrderId;
  }

  public CreateRestaurantOrderReply(long restaurantOrderId) {

    this.restaurantOrderId = restaurantOrderId;
  }

  public long getRestaurantOrderId() {
    return restaurantOrderId;
  }
}

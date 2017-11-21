package net.chrisrichardson.ftgo.restaurantservice.web;

public class CreateRestaurantResponse {
  private long id;

  public CreateRestaurantResponse() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public CreateRestaurantResponse(long id) {
    this.id = id;
  }
}

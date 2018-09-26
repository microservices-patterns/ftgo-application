package net.chrisrichardson.ftgo.restaurantservice.lambda;

public class GetRestaurantResponse {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GetRestaurantResponse(String name) {
    this.name = name;

  }
}

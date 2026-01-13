package net.chrisrichardson.ftgo.restaurantservice.web;

public class GetRestaurantResponse {
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private String name;

  public GetRestaurantResponse() {
  }

  public GetRestaurantResponse(Long id, String name) {
    this.id = id;
    this.name = name;
  }
}

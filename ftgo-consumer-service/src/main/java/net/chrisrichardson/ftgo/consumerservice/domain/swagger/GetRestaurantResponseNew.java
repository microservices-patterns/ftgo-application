package net.chrisrichardson.ftgo.consumerservice.domain.swagger;

public class GetRestaurantResponseNew {
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

  public GetRestaurantResponseNew() {
  }

  public GetRestaurantResponseNew(Long id, String name) {
    this.id = id;
    this.name = name;
  }
}

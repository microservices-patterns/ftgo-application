package net.chrisrichardson.ftgo.consumerservice.domain.swagger;

public class DeliveryInfo {

  private long id;
  private DeliveryState state;

  public DeliveryInfo() {
  }

  public DeliveryInfo(long id, DeliveryState state) {

    this.id = id;
    this.state = state;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public DeliveryState getState() {
    return state;
  }

  public void setState(DeliveryState state) {
    this.state = state;
  }
}

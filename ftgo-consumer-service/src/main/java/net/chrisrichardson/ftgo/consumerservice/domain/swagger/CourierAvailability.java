package net.chrisrichardson.ftgo.consumerservice.domain.swagger;

public class CourierAvailability {

  private boolean available;

  public CourierAvailability() {
  }

  public CourierAvailability(boolean available) {
    this.available = available;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }
}

package net.chrisrichardson.ftgo.aggregaterootexample;

import java.time.LocalDateTime;

public class RestaurantOrderExample extends AbstractAggregateRoot {

  private LocalDateTime acceptTime;
  private LocalDateTime readyBy;

  public void accept(LocalDateTime readyBy) {
    this.acceptTime = LocalDateTime.now();
    this.readyBy = readyBy;
    registerDomainEvent(new RestaurantOrderAcceptedEvent(readyBy));
  }

}

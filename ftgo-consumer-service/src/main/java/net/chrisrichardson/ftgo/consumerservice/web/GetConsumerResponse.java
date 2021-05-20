package net.chrisrichardson.ftgo.consumerservice.web;

import net.chrisrichardson.ftgo.common.PersonName;

public class GetConsumerResponse extends CreateConsumerResponse {
  private PersonName name;
  private long consumerId;

  public PersonName getName() {
    return name;
  }

  public long getConsumerId() {
    return consumerId;
  }

  public void setConsumerId(long consumerId) {
    this.consumerId = consumerId;
  }

  public GetConsumerResponse(PersonName name, long consumerId) {
    this.name = name;
    this.consumerId = consumerId;
  }
}

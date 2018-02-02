package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.messaging.common.Message;

public class MessageWithDestination {
  private final String destination;
  private final Message message;

  public MessageWithDestination(String destination, Message message) {
    this.destination = destination;
    this.message = message;
  }

  public String getDestination() {
    return destination;
  }

  public Message getMessage() {
    return message;
  }
}

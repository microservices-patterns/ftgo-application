package net.chrisrichardson.ftgo.cqrs.orderhistory.messaging;

import io.eventuate.tram.consumer.kafka.DuplicateMessageDetector;

// TODO Duplicate
public class NoopDuplicateMessageDetector implements DuplicateMessageDetector {

  @Override
  public boolean isDuplicate(String consumerId, String messageId) {
    return false;
  }
}

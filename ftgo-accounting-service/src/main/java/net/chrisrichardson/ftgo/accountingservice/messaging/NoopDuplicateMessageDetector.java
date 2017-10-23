package net.chrisrichardson.ftgo.accountingservice.messaging;

import io.eventuate.tram.consumer.kafka.DuplicateMessageDetector;

public class NoopDuplicateMessageDetector implements DuplicateMessageDetector {

  @Override
  public boolean isDuplicate(String consumerId, String messageId) {
    return false;
  }
}

package net.chrisrichardson.ftgo.cqrs.orderhistory.messaging;

import io.eventuate.tram.consumer.common.DuplicateMessageDetector;
import io.eventuate.tram.consumer.common.SubscriberIdAndMessage;

// TODO Duplicate
public class NoopDuplicateMessageDetector implements DuplicateMessageDetector {

  @Override
  public boolean isDuplicate(String consumerId, String messageId) {
    return false;
  }

  @Override
  public void doWithMessage(SubscriberIdAndMessage subscriberIdAndMessage, Runnable callback) {
    callback.run();
  }
}

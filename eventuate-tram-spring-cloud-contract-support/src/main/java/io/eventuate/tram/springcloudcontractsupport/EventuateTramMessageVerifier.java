package io.eventuate.tram.springcloudcontractsupport;

import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.messaging.producer.MessageBuilder;
import io.eventuate.tram.messaging.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singleton;

public class EventuateTramMessageVerifier implements MessageVerifier<Message> {

  @Autowired
  private MessageProducer messageProducer;

  @Autowired
  private MessageConsumer messageConsumer;

  @Override
  public void send(Message message, String destination) {
    throw new UnsupportedOperationException();
  }

  private ConcurrentHashMap<String, LinkedBlockingQueue<Message>> messagesByDestination = new ConcurrentHashMap<>();

  public EventuateTramMessageVerifier() {
  }

  @PostConstruct
  public void subscribe() {
    messageConsumer.subscribe("etmv", singleton("*"), m -> {
      String destination = m.getRequiredHeader(Message.DESTINATION);
      getForDestination(destination).add(m);
    });
  }

  private LinkedBlockingQueue<Message> getForDestination(String destination) {
    return messagesByDestination.computeIfAbsent(destination, k -> new LinkedBlockingQueue<>());
  }

  @Override
  public <T> void send(T payload, Map<String, Object> headers, String destination) {
    String p = (String) payload;
    MessageBuilder mb = MessageBuilder.withPayload(p);

    headers.forEach((key, value) -> {
      mb.withHeader(key, (String) value);
    });

    messageProducer.send(destination, mb.build());
  }

  @Override
  public Message receive(String destination, long timeout, TimeUnit timeUnit) {
    Message m;
    try {
      m = getForDestination(destination).poll(timeout, timeUnit);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    if (m == null)
      return null;
    return m;
  }

  @Override
  public Message receive(String destination) {
    return receive(destination, 5, TimeUnit.SECONDS);
  }
}

package net.chrisrichardson.ftgo.consumerservice.domain;

import io.eventuate.tram.events.common.DomainEvent;

public class ConsumerCreated implements DomainEvent {
    private Long consumerId;
    private ConsumerCreated() {
    }

    public ConsumerCreated(Long consumerId) {
        this.consumerId = consumerId;
    }

    public Long getConsumerId() {
        return consumerId;
    }
}

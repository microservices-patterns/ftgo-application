package net.chrisrichardson.ftgo.consumerservice.domain;

import io.eventuate.tram.events.common.DomainEvent;

public class ConsumerCreated implements DomainEvent {
    private Long consumerId;

    public ConsumerCreated(){}

    public ConsumerCreated(Long id){
        this.consumerId = id;
    }

    public Long getConsumerId() {
        return consumerId;
    }
}

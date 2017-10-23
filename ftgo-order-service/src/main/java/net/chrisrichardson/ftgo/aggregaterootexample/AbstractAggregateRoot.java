package net.chrisrichardson.ftgo.aggregaterootexample;


import io.eventuate.tram.events.common.DomainEvent;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractAggregateRoot {

  private List<DomainEvent> domainEvents = new LinkedList<>();

  public List<DomainEvent> getDomainEvents() {
    return domainEvents;
  }

  public void registerDomainEvent(DomainEvent event) {
    domainEvents.add(event);
  }
}

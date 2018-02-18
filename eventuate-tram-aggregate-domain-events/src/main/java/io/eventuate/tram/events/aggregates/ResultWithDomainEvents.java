package io.eventuate.tram.events.aggregates;

import io.eventuate.tram.events.common.DomainEvent;

import java.util.Arrays;
import java.util.List;

public class ResultWithDomainEvents<A, E extends DomainEvent> {

  public final A result;
  public final List<E> events;

  public ResultWithDomainEvents(A result, List<E> events) {
    this.result = result;
    this.events = events;
  }

  public ResultWithDomainEvents(A result, E... events) {
    this.result = result;
    this.events = Arrays.asList(events);
  }
}
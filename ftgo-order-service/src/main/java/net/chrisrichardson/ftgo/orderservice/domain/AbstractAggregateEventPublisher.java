package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderCreatedEvent;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class AbstractAggregateEventPublisher<T> {
  private Function<T, Object> idSupplier;
  private List<Class<? extends DomainEvent>> eventTypes;
  private DomainEventPublisher eventPublisher;
  private Class<T> aggregateType;

  protected AbstractAggregateEventPublisher(DomainEventPublisher eventPublisher,
                                            Class<T> aggregateType,
                                            Function<T, Object> idSupplier,
                                            Class<? extends DomainEvent>... eventTypes) {
    this.eventPublisher = eventPublisher;
    this.aggregateType = aggregateType;
    this.idSupplier = idSupplier;
    this.eventTypes = asList(eventTypes);
  }

  public void publish(T aggregate, List<DomainEvent> events) {
    verifyEvents(events);
    eventPublisher.publish(aggregateType, idSupplier.apply(aggregate), events);
  }

  protected void verifyEvents(List<DomainEvent> events) {
    events.forEach(event -> {
      if (eventTypes.stream().noneMatch(eventClass -> eventClass.isInstance(event)))
        throw new RuntimeException(String.format("event %s is not one of the allowed types %s", event, eventTypes));
    });
  }
}

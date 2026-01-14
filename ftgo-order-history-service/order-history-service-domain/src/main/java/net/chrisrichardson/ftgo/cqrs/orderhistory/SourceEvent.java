package net.chrisrichardson.ftgo.cqrs.orderhistory;

public class SourceEvent {

  private final String aggregateType;
  private final String aggregateId;
  private final String eventId;

  public SourceEvent(String aggregateType, String aggregateId, String eventId) {
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.eventId = eventId;
  }

  public String getAggregateType() {
    return aggregateType;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public String getEventId() {
    return eventId;
  }
}

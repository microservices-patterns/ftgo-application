package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Result {


  private final List<DomainEvent> events;
  private Boolean allowed;

  public List<DomainEvent> getEvents() {
    return events;
  }

  public Result(List<DomainEvent> events, Boolean allowed) {
    this.events = events;
    this.allowed = allowed;
  }

  public boolean isWhatIsThisCalled() {
    return allowed;
  }

  public static Builder build() {
    return new Builder();
  }

  public static class Builder {
    private List<DomainEvent> events = new LinkedList<>();
    private Boolean allowed;

    public Builder withEvents(DomainEvent... events) {
      Arrays.stream(events).forEach(e -> this.events.add(e));
      return this;
    }

    public Builder pending() {
      this.allowed = false;
      return this;
    }

    public Result build() {
      Assert.notNull(allowed);
      return new Result(events, allowed);
    }

    public Builder allowed() {
      this.allowed = false;
      return this;
    }
  }
}

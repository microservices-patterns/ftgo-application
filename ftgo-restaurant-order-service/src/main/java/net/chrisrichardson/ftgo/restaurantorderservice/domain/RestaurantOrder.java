package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import io.eventuate.tram.events.ResultWithEvents;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import net.chrisrichardson.ftgo.common.NotYetImplementedException;
import net.chrisrichardson.ftgo.common.UnsupportedStateTransitionException;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderDetails;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderLineItem;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Entity
@Table(name = "restaurant_orders")
@Access(AccessType.FIELD)
public class RestaurantOrder {

  @Id
  private Long id;

  @Enumerated(EnumType.STRING)
  private RestaurantOrderState state;

  private RestaurantOrderState previousState;

  private Long restaurantId;

  @ElementCollection
  @CollectionTable(name = "restaurant_order_line_items")
  private List<RestaurantOrderLineItem> lineItems;

  private LocalDateTime readyBy;
  private LocalDateTime acceptTime;
  private LocalDateTime preparingTime;
  private LocalDateTime pickedUpTime;
  private LocalDateTime readyForPickupTime;

  public static ResultWithDomainEvents<RestaurantOrder, RestaurantOrderDomainEvent> create(long restaurantId, Long id, RestaurantOrderDetails details) {
    return new ResultWithDomainEvents<>(new RestaurantOrder(restaurantId, id, details));
  }

  private RestaurantOrder() {
  }

  public RestaurantOrder(long restaurantId, Long id, RestaurantOrderDetails details) {
    this.restaurantId = restaurantId;
    this.id = id;
    this.state = RestaurantOrderState.CREATE_PENDING;
    this.lineItems = details.getLineItems();
  }

  public List<RestaurantOrderDomainEvent> confirmCreate() {
    switch (state) {
      case CREATE_PENDING:
        state = RestaurantOrderState.CREATED;
        return singletonList(new RestaurantOrderCreatedEvent(id, new RestaurantOrderDetails()));
      default:
        throw new UnsupportedStateTransitionException(state);
    }
  }

  public List<RestaurantOrderDomainEvent> cancelCreate() {
    throw new NotYetImplementedException();
  }


  public List<RestaurantOrderDomainEvent> accept(LocalDateTime readyBy) {
    switch (state) {
      case CREATED:
        // Verify that readyBy is in the futurestate = RestaurantOrderState.ACCEPTED;
        this.acceptTime = LocalDateTime.now();
        if (!acceptTime.isBefore(readyBy))
          throw new IllegalArgumentException("readyBy is not in the future");
        this.readyBy = readyBy;
        return singletonList(new RestaurantOrderAcceptedEvent(readyBy));
      default:
        throw new UnsupportedStateTransitionException(state);
    }
  }

  // TODO reject()

  // TODO cancel()

  public List<RestaurantOrderDomainEvent> preparing() {
    switch (state) {
      case ACCEPTED:
        this.state = RestaurantOrderState.PREPARING;
        this.preparingTime = LocalDateTime.now();
        return singletonList(new RestaurantOrderPreparationStartedEvent());
      default:
        throw new UnsupportedStateTransitionException(state);
    }
  }

  public List<RestaurantOrderDomainEvent> readyForPickup() {
    switch (state) {
      case PREPARING:
        this.state = RestaurantOrderState.READY_FOR_PICKUP;
        this.readyForPickupTime = LocalDateTime.now();
        return singletonList(new RestaurantOrderPreparationCompletedEvent());
      default:
        throw new UnsupportedStateTransitionException(state);
    }
  }

  public List<RestaurantOrderDomainEvent> pickedUp() {
    switch (state) {
      case READY_FOR_PICKUP:
        this.state = RestaurantOrderState.PICKED_UP;
        this.pickedUpTime = LocalDateTime.now();
        return singletonList(new RestaurantOrderPickedUpEvent());
      default:
        throw new UnsupportedStateTransitionException(state);
    }
  }

  public void changeLineItemQuantity() {
    switch (state) {
      case CREATED:
        // TODO
        break;
      case PREPARING:
        // TODO - too late
        break;
      default:
        throw new UnsupportedStateTransitionException(state);
    }

  }

  public List<RestaurantOrderDomainEvent> cancel() {
    switch (state) {
      case CREATED:
      case ACCEPTED:
        this.previousState = state;
        this.state = RestaurantOrderState.CANCEL_PENDING;
        return emptyList();
      default:
        throw new UnsupportedStateTransitionException(state);
    }
  }

  public Long getId() {
    return id;
  }

  public List<RestaurantOrderDomainEvent> confirmCancel() {
    switch (state) {
      case CANCEL_PENDING:
        this.state = RestaurantOrderState.CANCELLED;
        return singletonList(new RestaurantOrderCancelled());
      default:
        throw new UnsupportedStateTransitionException(state);

    }
  }
  public List<RestaurantOrderDomainEvent> undoCancel() {
    switch (state) {
      case CANCEL_PENDING:
        this.state = this.previousState;
        return emptyList();
      default:
        throw new UnsupportedStateTransitionException(state);

    }
  }

  public List<RestaurantOrderDomainEvent> beginReviseOrder(Map<String, Integer> revisedLineItemQuantities) {
    switch (state) {
      case CREATED:
      case ACCEPTED:
        this.previousState = state;
        this.state = RestaurantOrderState.REVISION_PENDING;
        return emptyList();
      default:
        throw new UnsupportedStateTransitionException(state);
    }
  }

  public List<RestaurantOrderDomainEvent> undoBeginReviseOrder() {
    switch (state) {
      case REVISION_PENDING:
        this.state = this.previousState;
        return emptyList();
      default:
        throw new UnsupportedStateTransitionException(state);
    }
  }

  public List<RestaurantOrderDomainEvent> confirmReviseRestaurantOrder(Map<String, Integer> revisedLineItemQuantities) {
    switch (state) {
      case REVISION_PENDING:
        this.state = this.previousState;
        return singletonList(new RestaurantOrderRevised());
      default:
        throw new UnsupportedStateTransitionException(state);

    }
  }
}

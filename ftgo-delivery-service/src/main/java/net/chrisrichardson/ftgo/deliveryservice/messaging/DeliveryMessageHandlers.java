package net.chrisrichardson.ftgo.deliveryservice.messaging;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.deliveryservice.domain.DeliveryService;
import net.chrisrichardson.ftgo.kitchenservice.api.KitchenServiceChannels;
import net.chrisrichardson.ftgo.kitchenservice.api.events.TicketAcceptedEvent;
import net.chrisrichardson.ftgo.kitchenservice.api.events.TicketCancelled;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderCreatedEvent;
import net.chrisrichardson.ftgo.restaurantservice.RestaurantServiceChannels;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;

import java.time.LocalDateTime;

public class DeliveryMessageHandlers {

  private DeliveryService deliveryService;

  public DeliveryMessageHandlers(DeliveryService deliveryService) {
    this.deliveryService = deliveryService;
  }

  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder
            .forAggregateType(KitchenServiceChannels.TICKET_EVENT_CHANNEL)
            .onEvent(TicketAcceptedEvent.class, this::handleTicketAcceptedEvent)
            .onEvent(TicketCancelled.class, this::handleTicketCancelledEvent)
            .andForAggregateType(OrderServiceChannels.ORDER_EVENT_CHANNEL)
            .onEvent(OrderCreatedEvent.class, this::handleOrderCreatedEvent)
            .andForAggregateType(RestaurantServiceChannels.RESTAURANT_EVENT_CHANNEL)
            .onEvent(RestaurantCreated.class, this::handleRestaurantCreated)
            .build();
  }

  public void handleRestaurantCreated(DomainEventEnvelope<RestaurantCreated> dee) {
    Address address = RestaurantEventMapper.toAddress(dee.getEvent().getAddress());
    deliveryService.createRestaurant(Long.parseLong(dee.getAggregateId()), dee.getEvent().getName(), address);
  }

  public void handleOrderCreatedEvent(DomainEventEnvelope<OrderCreatedEvent> dee) {
    Address address = dee.getEvent().getDeliveryAddress();
    deliveryService.createDelivery(Long.parseLong(dee.getAggregateId()),
            dee.getEvent().getOrderDetails().getRestaurantId(), address);
  }

  public void handleTicketAcceptedEvent(DomainEventEnvelope<TicketAcceptedEvent> dee) {
    LocalDateTime readyBy = dee.getEvent().getReadyBy();
    deliveryService.scheduleDelivery(Long.parseLong(dee.getAggregateId()), readyBy);
  }

  public void handleTicketCancelledEvent(DomainEventEnvelope<TicketCancelled> dee) {
    deliveryService.cancelDelivery(Long.parseLong(dee.getAggregateId()));
  }


}

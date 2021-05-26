package net.chrisrichardson.ftgo.kitchenservice.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import net.chrisrichardson.ftgo.common.RevisedOrderLineItem;
import net.chrisrichardson.ftgo.kitchenservice.api.TicketDetails;
import net.chrisrichardson.ftgo.kitchenservice.api.TicketLineItem;
import net.chrisrichardson.ftgo.kitchenservice.api.events.TicketDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class KitchenService {

  @Autowired
  private TicketRepository ticketRepository;

  @Autowired
  private TicketDomainEventPublisher domainEventPublisher;

  @Autowired
  private RestaurantRepository restaurantRepository;

  private Logger logger = LoggerFactory.getLogger(getClass());

  public void createMenu(long id, RestaurantMenu menu, long efficiency) {
    Restaurant restaurant = new Restaurant(id, menu.getMenuItems(), efficiency);
    restaurantRepository.save(restaurant);
  }

  public void reviseMenu(long ticketId, RestaurantMenu revisedMenu) {
    Restaurant restaurant = restaurantRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    restaurant.reviseMenu(revisedMenu);
  }

  public Ticket createTicket(long restaurantId, Long ticketId, TicketDetails ticketDetails) {
    if (restaurantRepository.findById(restaurantId).isPresent() &&  ticketDetails.getLineItems().stream().map(TicketLineItem::getQuantity).reduce(Integer::sum).isPresent() &&
            (restaurantRepository.findById(restaurantId).get().getEfficiency() < ticketDetails.getLineItems().stream().map(TicketLineItem::getQuantity).reduce(Integer::sum).get())) {
      logger.info("COS INNEGO NIZ DUPA");
      throw new RestaurantCapacityExceededException();
    }
    logger.info("DUPA2");
    ResultWithDomainEvents<Ticket, TicketDomainEvent> rwe = Ticket.create(restaurantId, ticketId, ticketDetails);
    ticketRepository.save(rwe.result);
    domainEventPublisher.publish(rwe.result, rwe.events);
    return rwe.result;
  }

  @Transactional
  public void accept(long ticketId, LocalDateTime readyBy) {
    Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    List<TicketDomainEvent> events = ticket.accept(readyBy);
    domainEventPublisher.publish(ticket, events);
  }

  public void confirmCreateTicket(Long ticketId) {
    Ticket ro = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    List<TicketDomainEvent> events = ro.confirmCreate();
    domainEventPublisher.publish(ro, events);
  }

  public void cancelCreateTicket(Long ticketId) {
    Ticket ro = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    List<TicketDomainEvent> events = ro.cancelCreate();
    domainEventPublisher.publish(ro, events);
  }


  public void cancelTicket(long restaurantId, long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    // TODO - verify restaurant id
    List<TicketDomainEvent> events = ticket.cancel();
    domainEventPublisher.publish(ticket, events);
  }


  public void confirmCancelTicket(long restaurantId, long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    // TODO - verify restaurant id
    List<TicketDomainEvent> events = ticket.confirmCancel();
    domainEventPublisher.publish(ticket, events);
  }

  public void undoCancel(long restaurantId, long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    // TODO - verify restaurant id
    List<TicketDomainEvent> events = ticket.undoCancel();
    domainEventPublisher.publish(ticket, events);

  }

  public void beginReviseOrder(long restaurantId, Long ticketId, List<RevisedOrderLineItem> revisedOrderLineItems) {
    Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    // TODO - verify restaurant id
    List<TicketDomainEvent> events = ticket.beginReviseOrder(revisedOrderLineItems);
    domainEventPublisher.publish(ticket, events);

  }

  public void undoBeginReviseOrder(long restaurantId, Long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    // TODO - verify restaurant id
    List<TicketDomainEvent> events = ticket.undoBeginReviseOrder();
    domainEventPublisher.publish(ticket, events);
  }

  public void confirmReviseTicket(long restaurantId, long ticketId, List<RevisedOrderLineItem> revisedOrderLineItems) {
    Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    // TODO - verify restaurant id
    List<TicketDomainEvent> events = ticket.confirmReviseTicket(revisedOrderLineItems);
    domainEventPublisher.publish(ticket, events);
  }


  // ...
}

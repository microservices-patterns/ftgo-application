package net.chrisrichardson.ftgo.kitchenservice.api.events;

import java.time.LocalDateTime;

public record TicketAcceptedEvent(LocalDateTime readyBy) implements TicketDomainEvent {
}

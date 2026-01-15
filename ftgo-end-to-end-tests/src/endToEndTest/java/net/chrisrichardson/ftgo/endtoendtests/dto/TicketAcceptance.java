package net.chrisrichardson.ftgo.endtoendtests.dto;

import java.time.LocalDateTime;

public class TicketAcceptance {
    private LocalDateTime readyBy;

    public TicketAcceptance() {
    }

    public TicketAcceptance(LocalDateTime readyBy) {
        this.readyBy = readyBy;
    }

    public LocalDateTime getReadyBy() {
        return readyBy;
    }
}

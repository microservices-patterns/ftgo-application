package net.chrisrichardson.ftgo.kitchenservice.contracts;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import net.chrisrichardson.ftgo.kitchenservice.api.events.TicketAcceptedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Collections;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                classes = {MessagingBase.TestConfig.class})
public abstract class MessagingBase {

    private static final String SAGA_REPLY_CHANNEL = "net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply";
    private static final String TICKET_AGGREGATE_TYPE = "net.chrisrichardson.ftgo.kitchenservice.domain.Ticket";

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    public static class TestConfig { }

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    // Reply triggers - called by contract tests for saga replies
    protected void createTicketReply() {
        messageProducer.send(SAGA_REPLY_CHANNEL,
                withSuccess(new CreateTicketReply(99L)));
    }

    protected void confirmCreateTicketReply() {
        messageProducer.send(SAGA_REPLY_CHANNEL, withSuccess());
    }

    // Event triggers - called by contract tests for domain events
    protected void ticketAcceptedEvent() {
        domainEventPublisher.publish(TICKET_AGGREGATE_TYPE, 99L,
                Collections.singletonList(new TicketAcceptedEvent(LocalDateTime.parse("2019-08-20T14:20:00.979"))));
    }
}

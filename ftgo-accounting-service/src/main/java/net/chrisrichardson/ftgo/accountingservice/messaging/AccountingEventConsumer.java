package net.chrisrichardson.ftgo.accountingservice.messaging;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import net.chrisrichardson.ftgo.accountingservice.domain.AccountingService;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class AccountingEventConsumer {

  @Autowired
  private AccountingService accountingService;

  private Logger logger = LoggerFactory.getLogger(getClass());

  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder
            .forAggregateType("net.chrisrichardson.ftgo.consumerservice.domain.Consumer")
            .onEvent(ConsumerCreated.class, this::createAccount) // TODO this is hack to get the correct package
            .build();
  }

  private void createAccount(DomainEventEnvelope<ConsumerCreated> dee) {
    String consumerId = dee.getAggregateId();
    accountingService.create(Long.parseLong(consumerId), consumerId);
  }

}

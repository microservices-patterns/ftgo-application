package net.chrisrichardson.ftgo.accountingservice.messaging;

import io.eventuate.javaclient.spring.EnableEventHandlers;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.consumer.common.DuplicateMessageDetector;
import io.eventuate.tram.events.common.DefaultDomainEventNameMapping;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.sagas.eventsourcingsupport.SagaReplyRequestedEventSubscriber;
import net.chrisrichardson.ftgo.accountingservice.domain.Account;
import net.chrisrichardson.ftgo.accountingservice.domain.AccountServiceConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;

@Configuration
@EnableEventHandlers
@Import({AccountServiceConfiguration.class, CommonConfiguration.class})
public class AccountingMessagingConfiguration {

  @Bean
  public AccountingEventConsumer accountingEventConsumer() {
    return new AccountingEventConsumer();
  }

  @Bean
  public DomainEventDispatcher domainEventDispatcher(AccountingEventConsumer accountingEventConsumer, MessageConsumer messageConsumer) {
    return new DomainEventDispatcher("accountingServiceDomainEventDispatcher",
            accountingEventConsumer.domainEventHandlers(),
            messageConsumer,
            new DefaultDomainEventNameMapping());
  }

  @Bean
  public AccountingServiceCommandHandler accountCommandHandler() {
    return new AccountingServiceCommandHandler();
  }


  @Bean
  public CommandDispatcher commandDispatcher(AccountingServiceCommandHandler target,
                                             AccountServiceChannelConfiguration data,
                                             MessageConsumer messageConsumer,
                                             MessageProducer messageProducer) {
    return new CommandDispatcher(data.getCommandDispatcherId(), target.commandHandlers(), messageConsumer, messageProducer);
  }

  @Bean
  public DuplicateMessageDetector duplicateMessageDetector() {
    return new NoopDuplicateMessageDetector();
  }

  @Bean
  public AccountServiceChannelConfiguration accountServiceChannelConfiguration() {
    return new AccountServiceChannelConfiguration("accountCommandDispatcher", "accountCommandChannel");
  }

  @Bean
  public SagaReplyRequestedEventSubscriber sagaReplyRequestedEventSubscriber() {
    return new SagaReplyRequestedEventSubscriber("accountingServiceSagaReplyRequestedEventSubscriber", Collections.singleton(Account.class.getName()));
  }

}

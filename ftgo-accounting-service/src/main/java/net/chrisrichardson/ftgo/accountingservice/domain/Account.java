package net.chrisrichardson.ftgo.accountingservice.domain;

import io.eventuate.Event;
import io.eventuate.ReflectiveMutableCommandProcessingAggregate;
import io.eventuate.tram.sagas.eventsourcingsupport.SagaReplyRequestedEvent;
import net.chrisrichardson.ftgo.common.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.eventuate.EventUtil.events;

public class Account extends ReflectiveMutableCommandProcessingAggregate<Account, AccountCommand> {

  public Money getBalance() {
    return balance;
  }

  public void setBalance(Money balance) {
    this.balance = balance;
  }

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Money balance;

  public List<Event> process(CreateAccountCommand command) {
    logger.info(command.getCustomerId().toString());
    return events(new AccountCreatedEvent(command.getCustomerId(), command.getInitialBalance()));
  }

  public void apply(AccountCreatedEvent event) {
    this.balance = event.getInitialBalance();
    logger.info(this.balance.asString());
    logger.info(event.getCustomerId().toString());
  }

  public List<Event> process(CheckAccountLimitCommandInternal command) {
    if(balance.isGreaterThanOrEqual(command.getMoney())){
      return events(new AccountLimitSufficientEvent());
    }
    //return events(new AccountLimitExceededEvent());
    throw new AccountLimitExceededException();
  }

  public List<Event> process(AuthorizeCommandInternal command) {
    return events(new AccountAuthorizedEvent());
  }

  public List<Event> process(ReverseAuthorizationCommandInternal command) {
    return Collections.emptyList();
  }
  public List<Event> process(ReviseAuthorizationCommandInternal command) {
    return Collections.emptyList();
  }

  public void apply(AccountAuthorizedEvent event) {

  }

  public void apply(AccountLimitSufficientEvent event) {
  }

  public void apply(AccountLimitExceededEvent event) {
  }

  public void apply(SagaReplyRequestedEvent event) {
    // TODO - need a way to not need this method
  }


}

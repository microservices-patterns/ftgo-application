package net.chrisrichardson.ftgo.accountingservice.domain;

import io.eventuate.sync.AggregateRepository;
import io.eventuate.EntityWithIdAndVersion;
import io.eventuate.SaveOptions;
import net.chrisrichardson.ftgo.common.Money;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AccountingService {
  @Autowired
  private AggregateRepository<Account, AccountCommand> accountRepository;

  public void create(Long consumerId, String aggregateId) {
    Money initialBalance = new Money(100);
    EntityWithIdAndVersion<Account> account = accountRepository.save(new CreateAccountCommand(consumerId, initialBalance),
            Optional.of(new SaveOptions().withId(aggregateId)));
  }
}

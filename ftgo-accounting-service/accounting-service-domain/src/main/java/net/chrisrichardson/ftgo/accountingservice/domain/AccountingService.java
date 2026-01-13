package net.chrisrichardson.ftgo.accountingservice.domain;

import io.eventuate.sync.AggregateRepository;
import io.eventuate.EntityWithIdAndVersion;
import io.eventuate.SaveOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AccountingService {
  @Autowired
  private AggregateRepository<Account, AccountCommand> accountRepository;

  public void create(String aggregateId) {
    EntityWithIdAndVersion<Account> account = accountRepository.save(new CreateAccountCommand(),
            Optional.of(new SaveOptions().withId(aggregateId)));
  }
}

package net.chrisrichardson.ftgo.accountingservice.domain;

import io.eventuate.sync.AggregateRepository;
import io.eventuate.sync.EventuateAggregateStore;
import io.eventuate.tram.spring.commands.producer.TramCommandProducerConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TramCommandProducerConfiguration.class, CommonConfiguration.class})
public class AccountServiceConfiguration {


  @Bean
  public AggregateRepository<Account, AccountCommand> accountRepositorySync(EventuateAggregateStore aggregateStore) {
    return new AggregateRepository<>(Account.class, aggregateStore);
  }

  @Bean
  public AccountingService accountingService() {
    return new AccountingService();
  }
}

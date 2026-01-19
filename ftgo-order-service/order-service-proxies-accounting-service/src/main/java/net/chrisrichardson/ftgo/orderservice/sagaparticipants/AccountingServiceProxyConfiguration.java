package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountingServiceProxyConfiguration {

  @Bean
  public AccountingServiceProxy accountingServiceProxy() {
    return new AccountingServiceProxy();
  }
}

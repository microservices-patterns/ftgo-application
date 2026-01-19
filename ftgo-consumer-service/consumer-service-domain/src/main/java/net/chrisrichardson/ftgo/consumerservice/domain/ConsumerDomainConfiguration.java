package net.chrisrichardson.ftgo.consumerservice.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerDomainConfiguration {

  @Bean
  public ConsumerService consumerService() {
    return new ConsumerService();
  }
}

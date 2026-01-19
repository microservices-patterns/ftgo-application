package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KitchenServiceProxyConfiguration {

  @Bean
  public KitchenServiceProxy kitchenServiceProxy() {
    return new KitchenServiceProxy();
  }
}

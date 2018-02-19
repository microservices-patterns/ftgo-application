package net.chrisrichardson.ftgo.apiagateway.consumers;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.Routes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.cloud.gateway.handler.predicate.RoutePredicates.method;
import static org.springframework.cloud.gateway.handler.predicate.RoutePredicates.path;

@Configuration
@EnableConfigurationProperties(ConsumerDestinations.class)
public class ConsumerConfiguration {

  @Bean
  public RouteLocator consumerProxyRouting(ConsumerDestinations consumerDestinations) {
    return Routes.locator()
            .route("orderService")
            .uri(consumerDestinations.getConsumerServiceUrl())
            .predicate(path("/consumers").and(method("POST").or(method("PUT"))))
            .and()
            .build();
  }

}

package net.chrisrichardson.ftgo.apiagateway.consumers;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(ConsumerDestinations.class)
public class ConsumerConfiguration {

  @Bean
  public RouteLocator consumerProxyRouting(RouteLocatorBuilder builder, ConsumerDestinations consumerDestinations) {
    return builder.routes()
            .route(r -> r.path("/consumers").and().method("POST").uri(consumerDestinations.getConsumerServiceUrl()))
            .route(r -> r.path("/consumers").and().method("PUT").uri(consumerDestinations.getConsumerServiceUrl()))
            .build();
  }

}

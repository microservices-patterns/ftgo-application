package net.chrisrichardson.ftgo.apiagateway.health;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class HealthConfiguration {


  @Bean
  public RouterFunction<ServerResponse> healthRouting() {
    return RouterFunctions.route(GET("/health"), this::health);
  }

  private Mono<ServerResponse> health(ServerRequest serverRequest) {
    return ServerResponse.ok().build();
  }

}

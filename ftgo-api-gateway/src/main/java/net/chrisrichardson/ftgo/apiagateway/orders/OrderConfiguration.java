package net.chrisrichardson.ftgo.apiagateway.orders;

import net.chrisrichardson.ftgo.apiagateway.proxies.AccountingService;
import net.chrisrichardson.ftgo.apiagateway.proxies.DeliveryService;
import net.chrisrichardson.ftgo.apiagateway.proxies.OrderServiceProxy;
import net.chrisrichardson.ftgo.apiagateway.proxies.RestaurantOrderService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.Routes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.cloud.gateway.handler.predicate.RoutePredicates.method;
import static org.springframework.cloud.gateway.handler.predicate.RoutePredicates.path;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@EnableConfigurationProperties(OrderDestinations.class)
public class OrderConfiguration {

  @Bean
  public RouteLocator orderProxyRouting(OrderDestinations orderDestinations) {
    return Routes.locator()
            .route("orderService")
            .uri(orderDestinations.getOrderServiceUrl())
            .predicate((path("/orders").and(method("POST").or(method("PUT")))).or(path("/orders/**").and(method("POST").or(method("PUT")))))
            .and()
            .route("orderHistoryService")
            .uri(orderDestinations.getOrderHistoryServiceUrl())
            .predicate(path("/orders").and(method("GET")))
            .and()
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> orderHandlerRouting(OrderHandlers orderHandlers) {
    return RouterFunctions.route(GET("/orders/{orderId}"), orderHandlers::getOrderDetails);
  }

  @Bean
  public OrderHandlers orderHandlers(OrderServiceProxy orderService, RestaurantOrderService restaurantOrderService,
                                     DeliveryService deliveryService, AccountingService accountingService) {
    return new OrderHandlers(orderService, restaurantOrderService, deliveryService, accountingService);
  }

  @Bean
  public WebClient webClient() {
    return WebClient.create();
  }

}

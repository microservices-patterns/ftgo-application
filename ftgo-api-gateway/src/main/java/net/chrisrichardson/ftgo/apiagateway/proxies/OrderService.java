package net.chrisrichardson.ftgo.apiagateway.proxies;

import net.chrisrichardson.ftgo.apiagateway.orders.OrderDestinations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OrderService {


  private OrderDestinations orderDestinations;

  private WebClient client;

  public OrderService(OrderDestinations orderDestinations, WebClient client) {
    this.orderDestinations = orderDestinations;
    this.client = client;
  }

  public Mono<OrderInfo> findOrderById(String orderId) {
    Mono<ClientResponse> response = client
            .get()
            .uri(orderDestinations.getOrderServiceUrl() + "/orders/{orderId}", orderId)
            .exchange();
    return response.flatMap(resp -> resp.bodyToMono(OrderInfo.class));
  }


}

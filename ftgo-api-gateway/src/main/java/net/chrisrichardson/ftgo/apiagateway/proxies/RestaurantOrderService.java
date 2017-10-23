package net.chrisrichardson.ftgo.apiagateway.proxies;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class RestaurantOrderService {
  public Mono<RestaurantOrderInfo> findRestaurantOrderByOrderId(String orderId) {
    return Mono.error(new UnsupportedOperationException());
  }
}

package net.chrisrichardson.ftgo.apiagateway.proxies;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class AccountingService {
  public Mono<BillInfo> findBillByOrderId(String orderId) {
    return Mono.error(new UnsupportedOperationException());
  }
}

package net.chrisrichardson.ftgo.orderservice.grpc;

import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(GrpcConfiguration.class)
public class OrderServiceGrpIntegrationTestConfiguration {

  @Bean
  public OrderService orderService() {
    return mock(OrderService.class);
  }
}

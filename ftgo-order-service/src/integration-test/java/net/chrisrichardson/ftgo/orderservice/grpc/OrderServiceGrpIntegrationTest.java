package net.chrisrichardson.ftgo.orderservice.grpc;


import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderJpaTestConfiguration;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceGrpIntegrationTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OrderServiceGrpIntegrationTest {

  @Autowired
  private OrderService orderService;

  @Test
  public void shouldCreateOrder() {

    Order order = new Order(1, 2, Collections.emptyList());
    order.setId(101L);

    when(orderService.createOrder(1, 2, Collections.emptyList())).thenReturn(order);
    OrderServiceClient client = new OrderServiceClient("localhost", 50051);

    long orderId = client.createOrder(1, 2, Collections.emptyList());

    assertEquals(101L, orderId);

  }
}

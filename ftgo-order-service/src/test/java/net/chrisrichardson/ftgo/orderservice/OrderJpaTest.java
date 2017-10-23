package net.chrisrichardson.ftgo.orderservice;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderJpaTestConfiguration.class)
public class OrderJpaTest {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Test
  public void shouldDoSomething() {

    Long orderId = transactionTemplate.execute((ts) -> {
      assertNotNull(orderRepository);
      long consumerId = -1;
      long restaurantId = -2;
      Money orderTotal = Money.ZERO;
      Order order = new Order(consumerId, restaurantId, Collections.singletonList(new OrderLineItem("samosas", "Samosas", new Money("2.50"), 3)));
      orderRepository.save(order);
      return order.getId();
    });


    transactionTemplate.execute((ts) -> {
      Order loadedOrder = orderRepository.findOne(orderId);

      assertNotNull(loadedOrder);
      assertEquals(1, loadedOrder.getLineItems().size());
      return null;
    });

  }

}
